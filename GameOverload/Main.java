import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application{
    
    public static void main(String[] args){
        launch(args);
    }
    
    @Override
    public void start(Stage window) throws Exception{
        window.setTitle("GAME OVERLOAD");
        window.setResizable(false);
        window.centerOnScreen();
        window.setWidth(500);
        window.setHeight(500);
        window.setOnCloseRequest(e -> System.exit(0));
        
        Pane layout=new Pane();
        
        try { //if error occured in image input, label will be displayed
            Image image = new Image("GameOverload.jpg");
            ImageView iv = new ImageView();
            iv.setImage(image);
            iv.setFitWidth(500);
            iv.setFitHeight(200);
            iv.setTranslateX(0);
            iv.setTranslateY(0);
            layout.getChildren().add(iv);            
        } catch (Exception e) {
            Label label=new Label("GAME OVERLOAD");
            label.setFont(new Font("Arial",50));
            label.setPrefSize(500, 150);
            label.setTranslateX(0);
            label.setTranslateY(0);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-background-color: black;");
            label.setTextFill(Color.web("#FFFFFF"));
            layout.getChildren().add(label);
        }
        
        ComboBox<String> gameList=new ComboBox<>();
        gameList.getItems().addAll("Snake Classic","Flappy Bird","Stick Runner");
        gameList.setPromptText("Choose a game");
        gameList.setPrefSize(250,20);
        gameList.setTranslateX(window.getWidth()/2-125);
        gameList.setTranslateY(window.getHeight()/2-10);          
        
        Button choose=new Button("GO!");
        choose.setPrefSize(125, 20);
        choose.setTranslateX(gameList.getTranslateX()+62.5);
        choose.setTranslateY(gameList.getTranslateY()+50);
        
        layout.setStyle("-fx-background-color: black");                
        layout.getChildren().addAll(gameList,choose);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        
        choose.setOnAction(e -> {
            switch(gameList.getSelectionModel().getSelectedIndex()){
                case 0:{
                    new SnakeGame();break;
                }case 1:{
                    new FlappyBird();break;
                }case 2:{
                    new StickRunner();break;
                }default:{
                    gameList.setPromptText("No game selected");
                }
            }
        });
        
        window.show();
    }
    
}
