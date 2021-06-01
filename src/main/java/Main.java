import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;

import java.sql.Time;

public class Main extends Application {
    public static final double col_wid = 19;
    public static final double row_wid = 19;
    public static final double W = 1425;    //window width
    public static final double H = 1015;    //window height
    String last_button = "block";           //this variable is used to track which button is being pressed down atm
    int[][] global_board_state = new int[50][75];  //tracks whether cells are dead/alive
    boolean manual = false;

    int frame_counter = 0;  //frame counter
    Timeline timeline;

    public int transition(int current_state, int neighbours){
        if(current_state == 1){
            if(neighbours < 2){
                return 0;
            }else if(neighbours == 2 || neighbours == 3){
                return 1;
            }else{
                return 0;
            }
        }else if(current_state == 0 && neighbours == 3){
            return 1;
        }
        return 0;
    }

    public void evaluate_board(){
        int[][] new_state = new int[50][75];
        for(int i=0; i < 50; i++) {
            for (int j = 0; j < 75; j++) {
                new_state[i][j] = 0;
            }
        }
        for(int i=0; i < 50; i++){
            for(int j=0; j < 75; j++){
                int neighbours = 0;
                if(i == 0){                                 //first row
                    neighbours += global_board_state[i+1][j];          //check directly below
                    if(j == 0){                             //top left
                        neighbours += global_board_state[i][j+1];
                        neighbours += global_board_state[i+1][j+1];
                    }else if(j == 74){                      //top right
                        neighbours += global_board_state[i][j-1];
                        neighbours += global_board_state[i+1][j-1];
                    }else{                                  //first row
                        neighbours += global_board_state[i][j-1];      //left
                        neighbours += global_board_state[i][j+1];      //right
                        neighbours += global_board_state[i+1][j-1];    //down left
                        neighbours += global_board_state[i+1][j+1];    //down right
                    }
                }else if(i == 49){                          //last row
                    neighbours += global_board_state[i-1][j];          //check directly above
                    if(j == 0){                             //bottom left
                        neighbours += global_board_state[i][j+1];
                        neighbours += global_board_state[i-1][j+1];
                    }else if(j == 74){                      //bottom right
                        neighbours += global_board_state[i][j-1];
                        neighbours += global_board_state[i-1][j-1];
                    }else{                                  //last row
                        neighbours += global_board_state[i][j-1];      //left
                        neighbours += global_board_state[i][j+1];      //right
                        neighbours += global_board_state[i-1][j-1];    //upper left
                        neighbours += global_board_state[i-1][j+1];    //upper right
                    }
                }else{
                    neighbours += global_board_state[i-1][j];          //above
                    neighbours += global_board_state[i+1][j];          //below
                    if(j == 0){                           //first column (excluding top left and bottom left)
                        neighbours += global_board_state[i][j+1];          //right
                        neighbours += global_board_state[i-1][j+1];        //upper right
                        neighbours += global_board_state[i+1][j+1];        //lower right
                    }else if(j == 74){                          //last column (excluding top right and bottom right)
                        neighbours += global_board_state[i][j-1];          //left
                        neighbours += global_board_state[i-1][j-1];        //upper left
                        neighbours += global_board_state[i+1][j-1];        //lower left
                    }else{                                      //every other square
                        neighbours += global_board_state[i][j+1];          //right
                        neighbours += global_board_state[i][j-1];          //left
                        neighbours += global_board_state[i-1][j+1];        //upper right
                        neighbours += global_board_state[i+1][j+1];        //lower right
                        neighbours += global_board_state[i-1][j-1];        //upper left
                        neighbours += global_board_state[i+1][j-1];        //lower left
                    }
                }
                new_state[i][j] = transition(global_board_state[i][j], neighbours);
            }
        }
        global_board_state = new_state;
//        System.out.println("GO");
//        for(int i = 0; i < 50; i++){
//            for(int j = 0; j < 75; j++){
//                System.out.print(global_board_state[i][j] + " ");
//            }
//            System.out.println("");
//        }
    }


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Conway's Game of Life (j333lu)");

        //Images on toolbar
        Image block = new Image("block.png", 30, 30, true, true);
        Image beehive = new Image("beehive.png", 30, 30, true, true);
        Image blinker = new Image("blinker.png", 30, 30, true, true);
        Image toad = new Image("toad.png", 30, 30, true, true);
        Image glider = new Image("glider.png", 30, 30, true, true);
        Image clear = new Image("clear.png", 30, 30, true, true);
        ImageView blockView = new ImageView(block);
        ImageView beehiveView = new ImageView(beehive);
        ImageView blinkerView = new ImageView(blinker);
        ImageView toadView = new ImageView(toad);
        ImageView gliderView = new ImageView(glider);
        ImageView clearView = new ImageView(clear);

        //buttons used on the toolbar
        Button block_button = new Button("Block", blockView);
        Button beehive_button = new Button("Beehive", beehiveView);
        Button blinker_button = new Button("Blinker", blinkerView);
        Button toad_button = new Button("Toad", toadView);
        Button glider_button = new Button("Glider", gliderView);
        Button clear_button = new Button("Clear", clearView);
        block_button.setPrefHeight(40);
        beehive_button.setPrefHeight(40);
        blinker_button.setPrefHeight(40);
        toad_button.setPrefHeight(40);
        glider_button.setPrefHeight(40);
        clear_button.setPrefHeight(40);

        //track which button is pressed
        block_button.setOnAction(event -> {
            last_button = "block";
        });
        beehive_button.setOnAction(event -> {
            last_button = "beehive";
        });
        blinker_button.setOnAction(event -> {
            last_button = "blinker";
        });
        toad_button.setOnAction(event -> {
            last_button = "toad";
        });
        glider_button.setOnAction(event -> {
            last_button = "glider";
        });


        //toolbar
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(block_button);
        toolbar.getItems().add(beehive_button);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(blinker_button);
        toolbar.getItems().add(toad_button);
        toolbar.getItems().add(glider_button);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(clear_button);
        toolbar.setMinHeight(50);
        toolbar.setMaxHeight(50);
        VBox box = new VBox(toolbar);

        //canvas
        Scene scene = new Scene(box, W, H, Color.WHITE);
        final Canvas canvas = new Canvas(W, H - 65);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //prints the 50x75 grid
        for(int j=0; j < 75; j++){
            for(int i=0; i < 50; i++){
                gc.setFill(Color.BLACK);
                gc.fillRect(j * row_wid, i * col_wid, row_wid, col_wid);
                gc.setFill(Color.WHITE);
                gc.fillRect(j * row_wid + 1, i * col_wid + 1, row_wid - 2, col_wid - 2);
            }
        }

        //mouse click to draw shapes
        //updates board state
        canvas.setOnMouseClicked(event -> {
            gc.setFill(Color.BLACK);
            if(last_button.equals("block")){
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19)] = 1;
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19) + 1] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19)] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19) + 1] = 1;
                gc.fillRect((Math.floor(event.getX()/19) * 19) + 2, (Math.floor(event.getY()/19) * 19) + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19 + 1) * 19) + 2, (Math.floor(event.getY()/19) * 19) + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19) * 19) + 2, (Math.floor(event.getY()/19 + 1) * 19) + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19 + 1) * 19) + 2, (Math.floor(event.getY()/19 + 1) * 19) + 2, row_wid - 4, col_wid - 4);
            }else if(last_button.equals("beehive")){
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19) + 1]= 1;
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19) + 2]= 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19)] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19) + 3] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 2][(int) Math.floor(event.getX()/19) + 2] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 2][(int) Math.floor(event.getX()/19) + 1] = 1;
                gc.fillRect((Math.floor(event.getX()/19) + 1) * 19 + 2, Math.floor(event.getY()/19) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19) + 2) * 19 + 2, Math.floor(event.getY()/19) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19)) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19) + 3) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19) + 2) * 19 + 2, Math.floor(event.getY()/19 + 2) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect((Math.floor(event.getX()/19) + 1) * 19 + 2, Math.floor(event.getY()/19 + 2) * 19 + 2, row_wid - 4, col_wid - 4);
            }else if(last_button.equals("blinker")){

                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19)] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19) + 1] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19) + 2] = 1;
                gc.fillRect(Math.floor(event.getX()/19) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 1) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 2) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                evaluate_board();
            }else if(last_button.equals("toad")){
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19) + 1] = 1;
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19) + 2] = 1;
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19) + 3] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19)] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19) + 2] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19) + 1] = 1;
                gc.fillRect(Math.floor(event.getX()/19 + 1) * 19 + 2, Math.floor(event.getY()/19) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 2) * 19 + 2, Math.floor(event.getY()/19) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 3) * 19 + 2, Math.floor(event.getY()/19) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 1) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 2) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
            }else if(last_button.equals("glider")){
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19)] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 2][(int) Math.floor(event.getX()/19) + 1] = 1;
                global_board_state[(int) Math.floor(event.getY()/19)][(int) Math.floor(event.getX()/19) + 2] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 1][(int) Math.floor(event.getX()/19) + 2] = 1;
                global_board_state[(int) Math.floor(event.getY()/19) + 2][(int) Math.floor(event.getX()/19) + 2] = 1;
                gc.fillRect(Math.floor(event.getX()/19) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 1) * 19 + 2, Math.floor(event.getY()/19 + 2) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 2) * 19 + 2, Math.floor(event.getY()/19) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 2) * 19 + 2, Math.floor(event.getY()/19 + 1) * 19 + 2, row_wid - 4, col_wid - 4);
                gc.fillRect(Math.floor(event.getX()/19 + 2) * 19 + 2, Math.floor(event.getY()/19 + 2) * 19 + 2, row_wid - 4, col_wid - 4);
            }
        });

        //clear board, rebuild grid
        clear_button.setOnAction(event -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            for(int j=0; j < 75; j++){
                for(int i=0; i < 50; i++){
                    global_board_state[i][j] = 0;
                    gc.setFill(Color.BLACK);
                    gc.fillRect(j * row_wid, i * col_wid, row_wid, col_wid);
                    gc.setFill(Color.WHITE);
                    gc.fillRect(j * row_wid + 1, i * col_wid + 1, row_wid - 2, col_wid - 2);
                }
            }
        });
        box.getChildren().add(canvas);

        //status bar
        Label frame = new Label("Frame " + frame_counter);
        VBox statusBar = new VBox(frame);
        statusBar.setAlignment(Pos.BOTTOM_RIGHT);
        statusBar.setPrefWidth(W);
        statusBar.setMaxHeight(15);
        statusBar.setMinHeight(15);
        box.getChildren().add(statusBar);

        //manual mode
        scene.addEventFilter(KeyEvent.KEY_PRESSED,
            value -> {
                KeyCode kc = value.getCode();
                if(kc.equals(KeyCode.M)){
                    manual = !manual;
                    if(manual){
                        System.out.println("Switched to Manual Mode. Press Spacebar to advance one frame.");
                        timeline.stop();
                        timeline.setCycleCount(1);
                    }else{
                        System.out.println("Switched to Auto Mode.");
                        timeline.setCycleCount(Timeline.INDEFINITE);
                        timeline.play();
                    }
                }else if(kc.equals(KeyCode.SPACE)){
                    if(manual){
                        timeline.play();
                    }
                }
            });


        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        evaluate_board();
                        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                        for(int j=0; j < 75; j++){
                            for(int i=0; i < 50; i++){
                                gc.setFill(Color.BLACK);
                                gc.fillRect(j * row_wid, i * col_wid, row_wid, col_wid);
                                gc.setFill(Color.WHITE);
                                gc.fillRect(j * row_wid + 1, i * col_wid + 1, row_wid - 2, col_wid - 2);
                            }
                        }
                        for(int i=0; i < 50; i++){
                            for(int j=0; j < 75; j++){
                                if(global_board_state[i][j] == 1){
                                    gc.setFill(Color.BLACK);
                                    gc.fillRect(j * row_wid + 2, i * col_wid + 2, row_wid - 4, col_wid - 4);
                                }
                            }
                        }
                        frame_counter++;
                        box.getChildren().remove(2);
                        Label frame = new Label("Frame " + frame_counter);
                        VBox statusBar = new VBox(frame);
                        statusBar.setAlignment(Pos.BOTTOM_RIGHT);
                        statusBar.setPrefWidth(W);
                        statusBar.setMaxHeight(15);
                        statusBar.setMinHeight(15);
                        box.getChildren().add(statusBar);
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        //add stuff to group here
        stage.setScene(scene);
        stage.show();
    }
}