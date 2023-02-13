package org.bitbuckets.lib.process;

import edu.wpi.first.networktables.*;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.ActionLevel;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.console.IConsole;
import org.bitbuckets.lib.tune.IForceBackTuner;
import org.bitbuckets.lib.tune.type.ValueTuner;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ProcessDriver implements IProcessDriver {



    public static class PreComputedRecord {
        final int parent; //exists so you can walk back up the tree
        final Path path;
        final IForceBackTuner<Boolean> tuner;
        final IForceBackTuner<ActionLevel> level;
        final IConsole console;

        public PreComputedRecord(int parent, Path path, IForceBackTuner<Boolean> tuner, IForceBackTuner<ActionLevel> level, IConsole console) {
            this.parent = parent;
            this.path = path;
            this.tuner = tuner;
            this.level = level;
            this.console = console;
        }
    }



    final Queue<ProcessQueueRecord> records = new ArrayDeque<>();
    final Map<Integer, List<Integer>> parentToChildMap = new HashMap<>();
    final Map<Integer, PreComputedRecord> childToRecordMap = new HashMap<>();

    int lastAllocatedId = 0;


    final NetworkTableInstance instance;
    final IConsole console;
    final NetworkTable table;

    public ProcessDriver(NetworkTableInstance instance, IConsole console, NetworkTable table) {
        this.instance = instance;
        this.console = console;
        this.table = table;
    }


    int allocate(int parent, Path path) {
        lastAllocatedId++;

        NetworkTableEntry console = table.getEntry(path.getAsTablePath() + "/" + "console-out");
        NetworkTableEntry setTuningEnabled = table.getEntry(path.getAsTablePath() + "/" + "set-tuning-enabled");
        NetworkTableEntry setActionLevel = table.getEntry(path.getAsTablePath() + "/" + "set-action-level");

        console.setString("initialized!");
        setTuningEnabled.setBoolean(true);
        setActionLevel.setString(ActionLevel.INFO.toString()); //default level

        IConsole console1 = new ProcessConsole();


        ValueTuner<Boolean> tuner = new ValueTuner<>(true);

        NetworkTableInstance.getDefault().addListener(setActionLevel, EnumSet.of(NetworkTableEvent.Kind.kValueAll), tuner);
        NetworkTableInstance.getDefault().addListener(setActionLevel, EnumSet.of(NetworkTableEvent.Kind.kValueAll), e -> {
            cascadeToValue(lastAllocatedId, e.valueData.value.getBoolean());
        });

        childToRecordMap.put(lastAllocatedId, new PreComputedRecord(parent, path, tuner, level, console1));

        return lastAllocatedId;
    }

    @Override
    public int rootProcess(String name) {
        Path newRootPath = new Path(new String[]{name});

        return allocate(Integer.MIN_VALUE, newRootPath);
    }

    @Override
    public int childProcess(int parentId, String name) {
        PreComputedRecord parent = childToRecordMap.get(parentId);
        if (parent == null) {
            console.sendError(new IllegalStateException("Parent id " + parentId + " does not exist but was asked for as child"));
            return Integer.MIN_VALUE; //TODO do something besides this
        }

        int selfId = allocate(parentId, parent.path.append(name));
        parentToChildMap.computeIfAbsent(parentId, i-> new ArrayList<>()).add(selfId);

        return selfId;
    }

    @Override
    public Path getPath(int id) {
        PreComputedRecord rec = childToRecordMap.get(id);
        if (rec == null) {
            console.sendError(new IllegalStateException("bad id: " + id));
            return null;
        }

        return rec.path;
    }


    void cascadeToValue(int id, boolean value) {

        List<Integer> ids = parentToChildMap.get(id);

        for (int childId : ids) {
            childToRecordMap.get(childId).tuner.forceToValue(value);
            cascadeToValue(childId, value);
        }

    }

    //this one is faster
    @Override
    public Supplier<Boolean> getProcessEnabled(int id) {
        var rec = childToRecordMap.get(id);

        if (rec == null) {
            console.sendError(new IllegalStateException("weird id is null: " + id));
            return null;
        }

        return rec.tuner::readValue;
    }

    @Override
    public IConsole getConsole(int id) {
        var rec = childToRecordMap.get(id);

        if (rec == null) {
            console.sendError(new IllegalStateException("weird id is null: " + id));
            return null;
        }

        return rec.console;
    }

    @Override
    public <T> ILoggable<T> getLoggable(int id) {


        return null;
    }


    @Override
    public boolean isProcessEnabled(int id) {
        var rec = childToRecordMap.get(id);
        if (rec == null) {
            console.sendError(new IllegalStateException("weird id is null: " + id));
            return false;
        }

        return rec.tuner.readValue();
    }




}
