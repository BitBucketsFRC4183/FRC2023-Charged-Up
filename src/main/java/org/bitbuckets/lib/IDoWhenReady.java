package org.bitbuckets.lib;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.process.RegisterType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IDoWhenReady {

    CompletableFuture<GenericEntry> doWhenReady(Function<ShuffleboardContainer, GenericEntry> fn, RegisterType type);
    CompletableFuture<Void> doWhenReady(Consumer<ShuffleboardContainer> container, RegisterType type);
    CompletableFuture<ShuffleboardContainer> doWhenReadyDbg();

}
