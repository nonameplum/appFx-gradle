package appFx;

import appFx.controllers.OrmLiteController;
import appFx.datasource.SqlLiteDBI;
import appFx.eventbus.EventBusContext;
import appFx.eventbus.events.SomethingEvent;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ViewContext;
import reactfx.EventSource;
import reactfx.Subscription;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Subscription subscribe;
    Notification.Notifier notifier;

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);

        ViewContext<OrmLiteController> viewContext = ViewFactory.getInstance().createByController(OrmLiteController.class);
        viewContext.getApplicationContext().register("db", new SqlLiteDBI());

        primaryStage.setTitle("appFx");
        primaryStage.setScene(new Scene((Parent) viewContext.getRootNode(), 500, 500));
        primaryStage.setOnCloseRequest(observable -> notifier.stop());
        primaryStage.show();

        notifier = Notification.Notifier.INSTANCE;
        notifier.setParentStage(primaryStage);

        EventSource<SomethingEvent> eventSource = EventBusContext.getInstance().getEventSource(SomethingEvent.class);
        if (eventSource != null) {
            subscribe = eventSource.subscribe(event -> {
                //notifier.notifyInfo("Update", "ID: " + event.getValue().getId() + "\nNAME: " + event.getValue().getName());

                Notifications notificationBuilder = Notifications.create()
                        .title("Update")
                        .text("ID: " + event.getValue().getId() + "\nNAME: " + event.getValue().getName())
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.BOTTOM_RIGHT);

                notificationBuilder.showInformation();
            });
        }
    }

    @Override
    public void stop() throws Exception {
        if (subscribe != null) {
            subscribe.unsubscribe();
        }
        super.stop();
    }
}
