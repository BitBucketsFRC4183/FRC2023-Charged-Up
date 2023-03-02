package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.*;
import edu.wpi.first.util.sendable.SendableRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

//FIXME this is awful
/**
 * I frankensteined up a sendable chooser
 * @param <V>
 */
public class ChooserTuner<V extends Enum<V>> implements NTSendable, AutoCloseable, IForceSendTuner<V> {

    private static final String DEFAULT = "default";
    private static final String SELECTED = "selected";
    private static final String ACTIVE = "active";
    private static final String OPTIONS = "options";
    private static final String INSTANCE = ".instance";

    private final Map<String, V> m_map = new LinkedHashMap<>();

    private final int m_instance;
    private static final AtomicInteger s_instances = new AtomicInteger();

    final Class<V> clazz;
    final V defaultData;
    private final List<Consumer<V>> listeners = new ArrayList<>();

    public ChooserTuner(Class<V> clazz, V defaultData) {
        this.clazz = clazz;
        this.defaultData = defaultData;
        m_instance = s_instances.getAndIncrement();
        SendableRegistry.add(this, "SendableChooser", m_instance);
    }

    @Override
    public void close() {
        SendableRegistry.remove(this);
        m_mutex.lock();
        try {
            for (StringPublisher pub : m_activePubs) {
                pub.close();
            }
        } finally {
            m_mutex.unlock();
        }
    }


    public void addOption(String name, V object) {
        m_map.put(name, object);
    }

    /**
     * Returns the selected option. If there is none selected, it will return the default. If there is
     * none selected and no default, then it will return {@code null}.
     *
     * @return the option selected
     */
    public V getSelected() {
        m_mutex.lock();
        try {
            if (m_selected != null) {
                return m_map.get(m_selected);
            } else {
                return defaultData;
            }
        } finally {
            m_mutex.unlock();
        }
    }

    private String m_selected;
    private final List<StringPublisher> m_activePubs = new ArrayList<>();
    private final ReentrantLock m_mutex = new ReentrantLock();

    @Override
    public void initSendable(NTSendableBuilder builder) {
        builder.setSmartDashboardType("String Chooser");
        IntegerPublisher instancePub = new IntegerTopic(builder.getTopic(INSTANCE)).publish();
        instancePub.set(m_instance);
        builder.addCloseable(instancePub);
        builder.addStringProperty(DEFAULT, defaultData::name, null);
        builder.addStringArrayProperty(OPTIONS, () -> m_map.keySet().toArray(new String[0]), null);
        builder.addStringProperty(
                ACTIVE,
                () -> {
                    m_mutex.lock();
                    try {
                        if (m_selected != null) {
                            return m_selected;
                        } else {
                            return defaultData.name();
                        }
                    } finally {
                        m_mutex.unlock();
                    }
                },
                null);
        m_mutex.lock();
        try {
            //i have no idea why they do this
            m_activePubs.add(new StringTopic(builder.getTopic(ACTIVE)).publish());
        } finally {
            m_mutex.unlock();
        }
        builder.addStringProperty(
                SELECTED,
                null,
                val -> {
                    forceToValue(Enum.valueOf(clazz, val));
                    runData(Enum.valueOf(clazz, val));
                });
    }

    public synchronized void runData(V data) {
        for (Consumer<V> consumer : listeners) {
            consumer.accept(data);
        }
    }

    @Override
    public void forceToValue(V value) {
        m_mutex.lock();
        try {
            for (StringPublisher publisher : m_activePubs) {
                publisher.set(value.name());
            }
            m_selected = value.name();
        } finally {
            m_mutex.unlock();
        }
    }

    @Override
    public V readValue() {
        return getSelected();
    }

    @Override
    public V consumeValue() {
        return getSelected();
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }



    @Override
    public void bind(Consumer<V> data) {
        listeners.add(data);
    }
}