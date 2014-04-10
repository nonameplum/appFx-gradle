package appFx;

import appFx.controllers.*;
import appFx.datasource.SqlLiteDBI;
import appFx.eventbus.events.*;
import appFx.eventbus.events.SimpleActionEvent;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ViewContext;
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

        EventBusContext.INSTANCE.eventBus.addListener(SomethingModelActionEvent.class, new EventListener<SomethingModelActionEvent>() {
            @Override
            public void notify(SomethingModelActionEvent e) {
                notifier.notifyError(Integer.toString(e.getModel().getId()), e.getModel().getName());
            }
        });

        EventBusContext.INSTANCE.eventBus.addListener(SimpleActionEvent.class, new EventListener<SimpleActionEvent>() {
            @Override
            public void notify(SimpleActionEvent e) {
                notifier.notifyWarning(e.getClass().getName(), e.getSomeText() + " SimpleActionEvent.class 1");
            }
        });

        EventBusContext.INSTANCE.eventBus.addListener(appFx.controllers.SimpleActionEvent.class, new EventListener<appFx.controllers.SimpleActionEvent>() {
            @Override
            public void notify(appFx.controllers.SimpleActionEvent e) {
                notifier.notifyInfo(e.getClass().getName(), e.getAnotherText() + " appFx.controllers.SimpleActionEvent.class");
            }
        });

        EventBusContext.INSTANCE.eventBus.addListener(SimpleActionEvent.class, new EventListener<SimpleActionEvent>() {
            @Override
            public void notify(SimpleActionEvent e) {
                notifier.notifySuccess(e.getClass().getName(), e.getSomeText() + " SimpleActionEvent.class 2");
            }
        });

    }

    @Override
    public void stop() throws Exception {
        if (subscribe != null) {
            subscribe.unsubscribe();
        }
        super.stop();
    }
}
