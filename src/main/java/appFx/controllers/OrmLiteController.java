package appFx.controllers;

import appFx.datasource.SqlLiteDBI;
import appFx.datasource.TableViewDS;
import appFx.eventbus.EventBusContext;
import appFx.eventbus.events.SomethingEvent;
import appFx.models.Something;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import org.controlsfx.control.PopOver;
import org.datafx.controller.FXMLController;
import org.datafx.controller.context.FXMLViewContext;
import org.datafx.controller.context.ViewContext;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import reactfx.EventSource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

@FXMLController("/fxml/main.fxml")
public class OrmLiteController {

    @FXML
    private TextField tfSelected;
    @FXML
    //private TableView<ObservableMap<String, SimpleObjectProperty<Object>>> tableView;
    private TableView<Something> tableView;
    @FXML
    private Button btnEdit;
    @FXMLViewContext
    private ViewContext<Controller> context;
    private TableViewDS tableViewDS;
    private PopOver popOver;

    ObservableList<Something> somethingList;

    JdbcConnectionSource jdbcConnectionSource;
    Dao<Something, ?> dao;

    JdbcPooledConnectionSource firebirdConnection;

    Random randomGenerator = new Random();
    EventSource<SomethingEvent> eventSource = new EventSource<>();

    @PostConstruct
    public void init() {
        EventBusContext.getInstance().registerEventSource(SomethingEvent.class, eventSource);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(false);

        try {
            jdbcConnectionSource = new JdbcPooledConnectionSource(SqlLiteDBI.getConnectionUrl());
//            firebirdConnection = new JdbcPooledConnectionSource(
//                    "jdbc:firebirdsql://cbi-sql/Dane/E_FIRMA/E_FIRMA.GDB",
//                    "topsupus",
//                    "topmaspa", new BaseDatabaseType() {
//                @Override
//                protected String getDriverClassName() {
//                    return "org.firebirdsql.jdbc.FBDriver";
//                }
//
//                @Override
//                public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
//                    return false;
//                }
//
//                @Override
//                public String getDatabaseName() {
//                    return null;
//                }
//            });
//
//            Dao<Something, ?> liteDao = DaoManager.createDao(firebirdConnection, Something.class);
//            GenericRawResults<Map<String, String>> rawResults =
//                    liteDao.queryRaw(
//                            "select * from EWI_POJAZDY",
//                            new RawRowMapper<Map<String, String>>() {
//                                public Map<String, String> mapRow(String[] columnNames,
//                                                  String[] resultColumns) {
//                                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
//                                    for (int i=0; i<columnNames.length; i++) {
//                                        map.put(columnNames[i], resultColumns[i]);
//                                    }
//                                    return map;
//                                }
//                            });
//
//            List<Map<String, String>> results = rawResults.getResults();
//            for(Map<String, String> map : results) {
//                System.out.println(map);
//            }

            dao = DaoManager.createDao(jdbcConnectionSource, Something.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            cfgPopupEdition();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cfgPopupEdition() throws IOException {
        final FXMLLoader popupFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popcontent.fxml"));
        final Parent popupRoot = popupFxmlLoader.load();

        popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
        popOver.setArrowSize(6);
        popOver.setCornerRadius(6);
        popOver.setDetachable(false);
        popOver.setAutoHide(true);

        final Label lblName = (Label) popupRoot.lookup("#lblName");
        popOver.setContentNode(popupRoot);

        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        final Button btnSave = (Button) popupRoot.lookupAll("#btnSave").toArray()[0];
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Something selectedItem = tableView.getSelectionModel().getSelectedItem();
                PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(selectedItem);

                for (int j = 0; j < tableView.getColumns().size(); j++) {
                    TableColumn<Something, ?> tableColumn = tableView.getColumns().get(j);
                    for (Node node : grid.getChildren()) {
                        if (node.getId() != null && node.getId().equals(tableColumn.getText().toLowerCase())) {
                            String key = tableColumn.getText().toLowerCase();
                            String value = ((TextField) node).getText();
                            propertyAccessor.setPropertyValue(tableColumn.getText().toLowerCase(), value);
                        }
                    }
                }

                try {
                    int update = dao.update(selectedItem);
                    eventSource.push(new SomethingEvent(selectedItem));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                popOver.hide();
            }
        });

        final AnchorPane anchorPane = (AnchorPane) popupRoot.lookupAll("#controlsContent").toArray()[0];
        anchorPane.getChildren().add(grid);

        final Button btnCancel = (Button) popupRoot.lookupAll("#btnCancel").toArray()[0];
        btnCancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                popOver.hide();
            }
        });

        final BoxBlur boxBlur = new BoxBlur();
        boxBlur.setWidth(3);
        boxBlur.setHeight(1);
        boxBlur.setIterations(5);

        popOver.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                tableView.setEffect(null);
                tableView.setDisable(false);
            }
        });

        popOver.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                tableView.setDisable(true);
            }
        });

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() < 2) {
                    popOver.hide();
                    tableView.getSelectionModel().selectedItemProperty();
                }
                if (mouseEvent.getClickCount() >= 2) {
                    Something selectedItem = tableView.getSelectionModel().getSelectedItem();
                    PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(selectedItem);

                    popOver.hide();

                    // Set name label
                    lblName.setText(selectedItem.getName());

                    // Create text fields for bean
                    grid.getChildren().clear();
                    for (int i = 0; i < tableView.getColumns().size(); i++) {
                        TableColumn<Something, ?> tableColumn = tableView.getColumns().get(i);

                        Label label = new Label(tableColumn.getText());
                        grid.add(label, 0, i);

                        TextField textField = new TextField();
                        textField.setId(tableColumn.getText().toLowerCase());

                        Object propertyValue = propertyAccessor.getPropertyValue(tableColumn.getText().toLowerCase());
                        textField.setText(propertyValue.toString());

                        grid.add(textField, 1, i);
                    }

                    tableView.setEffect(boxBlur);
                    // Show popup
                    popOver.show(tableView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            }
        });
    }

    public void onLoadData() throws SQLException {
        somethingList = FXCollections.observableList(dao.queryForAll());
        tableView.getColumns().clear();

        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<Something, Integer>("id"));
        TableColumn nameColumn = new TableColumn("NAME");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Something, String>("name"));

        tableView.getColumns().addAll(idColumn, nameColumn);
        tableView.setItems(FXCollections.observableArrayList(somethingList));

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Something>() {
            @Override
            public void changed(ObservableValue<? extends Something> observableValue, Something oldValue, Something newValue) {
                if (oldValue != null) {
                    tfSelected.textProperty().unbindBidirectional(oldValue.nameProperty());
                }
                if (newValue != null) {
                    tfSelected.textProperty().bindBidirectional(newValue.nameProperty());
                }
            }
        });
    }

    public void onEdit() throws SQLException {
        Something item = somethingList.get(0);
        Integer randomInt = randomGenerator.nextInt(100);
        item.setName("Name " + randomInt.toString());
        int updated = dao.update(item);
        updated += 1;
        System.out.println(updated);

        eventSource.push(new SomethingEvent(item));
    }

}