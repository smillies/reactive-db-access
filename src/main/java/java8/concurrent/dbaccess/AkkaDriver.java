package java8.concurrent.dbaccess;

import static java.util.concurrent.TimeUnit.MINUTES;

import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

public class AkkaDriver {
    

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("application");
        system.actorOf(Props.create(AkkaMentionsFetcher.class, args[0]), "fetcher");

        system.scheduler().scheduleOnce(Duration.create(15, MINUTES), () -> system.terminate(), system.dispatcher());
    }

}
