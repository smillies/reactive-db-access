package java8.concurrent.dbaccess;

import java.util.concurrent.CompletableFuture;

import akka.actor.ActorRef;

public interface PipeToEnabled {
    
    default CompletableFuture<Void> pipeTo(ActorRef target, CompletableFuture<?> future) {
        assert !future.isCompletedExceptionally();
        return future.thenAccept(s -> target.tell(s, null));
    }
    
}
