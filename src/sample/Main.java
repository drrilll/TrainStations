package sample;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.PickResult;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Main extends Application {

    public static final int GRIDX = 30;
    public static final int GRIDY = 30;
    public static final int BUFFERX = 2;
    public static final int BUFFERY = 2;
    public static final int SIZEX = 1024;
    public static final int SIZEY = 1024;


    Label collision;

    @Override
    public void start(Stage primaryStage) throws Exception{





        primaryStage.setTitle("Train Station");

        MenuBar bar = new MenuBar();
        Menu menu = new Menu("Station Layout");
        MenuItem square, diagonal;
        menu.getItems().add(square = new MenuItem("Square"));
        menu.getItems().add(diagonal = new MenuItem("Diagonal"));
        bar.getMenus().add(menu);
        TextField numStations = new TextField();
        TextField carLength = new TextField();
        TextField numColors = new TextField();
        SplitPane grid = new SplitPane();
        GridPane controlPane = new GridPane();
        Button pause = new Button("Pause/Play");
        Button reverse = new Button("Forward/Reverse");
        Button forward = new Button("Step Forward");
        Button back = new Button ("Step Back");

        TextField eastColor = new TextField();
        TextField northColor = new TextField();
        TextField westColor = new TextField();
        TextField southColor = new TextField();

        Button optimalColoring = new Button("Optimal Colouring");

/*

        Pane root = getCircle();




        //VBox grid = new VBox(bar,textField,button,root);
        Scene scene = new Scene(grid, 800,800);
        primaryStage.setScene(scene);
        primaryStage.show();

        */

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(1);

        collision = new Label();


        Group root = new Group();
        int yaxis = 0;
        controlPane.add(bar,0,yaxis++);
        controlPane.add(numStations, 1, yaxis);
        controlPane.add(new Label("Number of Stations:"), 0, yaxis++);
        controlPane.add(carLength,1,yaxis);
        controlPane.add(new Label("Car length: "), 0, yaxis++);
        controlPane.add(new Label("Number of Colors: "), 0, yaxis);
        controlPane.add(numColors, 1, yaxis++);
        controlPane.add(pause,0,yaxis);
        controlPane.add(reverse,1,yaxis++);
        controlPane.add(back,0,yaxis);
        controlPane.add(forward,1,yaxis++);
        controlPane.add(slider, 0,yaxis);
        controlPane.add(new Label("Time slider"),1,yaxis++);
        controlPane.add(new Label("Northbound Colors"),0,yaxis);
        controlPane.add(northColor,1,yaxis++);
        controlPane.add(new Label("Eastbound Colors"),0,yaxis);
        controlPane.add(eastColor,1,yaxis++);
        controlPane.add(new Label("Southbound Colors"),0,yaxis);
        controlPane.add(southColor,1,yaxis++);
        controlPane.add(new Label("Westbound Colors"),0,yaxis);
        controlPane.add(westColor,1,yaxis++);
        controlPane.add(optimalColoring, 0, yaxis++);
        controlPane.add(collision,0,yaxis);


        //grid.add(controlPane);
        //grid.add(root);

        grid.getItems().add(controlPane);
        grid.getItems().add(root);




        Scene theScene = new Scene( grid );
        primaryStage.setScene( theScene );

        Canvas canvas = new Canvas( SIZEX, SIZEY );
        root.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount( Timeline.INDEFINITE );

        Controller controller = new Controller(this, gameLoop, gc);



        square.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.changeLayout(Controller.Layout.square);
            }
        });
        diagonal.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.changeLayout(Controller.Layout.diagonal);
            }
        });
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.togglePaused();
            }
        });
        reverse.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.reverse();
            }
        });
        forward.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.stepForward();
            }
        });
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.stepBack();
            }
        });
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                controller.pause();
                controller.setTime(new_val);
                controller.update();
            }
        });
        northColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.setNorthColors(northColor.getText());
            }
        });
        eastColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.setEastColors(eastColor.getText());
            }
        });
        southColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.setSouthColors(southColor.getText());
            }
        });
        westColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.setWestColors(westColor.getText());
            }
        });
        numStations.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.setNumStations(Integer.parseInt(numStations.getText()));
                controller.initialize();
            }
        });
        carLength.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.setCarLength(Integer.parseInt(carLength.getText()));
            }
        });
        optimalColoring.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.optimalColouring();
            }
        });
        numColors.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Station.setColors(Integer.parseInt(numColors.getText()));
            }
        });



        KeyFrame kf = new KeyFrame(
                Duration.seconds(0.017),                // 60 FPS
                new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent ae)
                    {
                        controller.passTheTime();

                        controller.update();

                    }
                });

        gameLoop.getKeyFrames().add( kf );
        try {
            controller.initialize();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        gameLoop.play();



        primaryStage.show();
    }

    public void collisionOn(ArrayList<Position> pos){
        StringBuffer buf = new StringBuffer("Collision at: \n");
        for(Position position: pos){
            buf.append("("+position.x + " "+ position.y+") \n" );
        }
        collision.setText(buf.toString());
    }

    public void collisionOff(){
        collision.setText("");
    }




    public static void main(String[] args) {
        launch(args);
    }
}
