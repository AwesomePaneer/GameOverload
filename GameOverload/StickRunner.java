import java.util.ArrayList;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class StickRunner {
    
        private Stage window;
        private final int height=400;
        private final int width=400;
        private boolean played=false;
        private int score=0;
        private boolean running=false;
        private Timeline t;
        private int ticks=0;
        private int ymotion=0;
        private ImageView sprite;
        private Line ground;
        private ArrayList<Rectangle> obstacles;
        private boolean jumping=false;
        private boolean upJump=false;
        private Rectangle collCheck;
        private Label scoreLabel;
        
        public StickRunner(){
            window=new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setHeight(height);
            window.setWidth(width);
            window.setMinHeight(height);
            window.setMinWidth(width);
            window.setMaxWidth(width);
            window.setMaxHeight(height);
            window.setResizable(false);        
            window.setTitle("STICK RUNNER"); 
            t=new Timeline();
            obstacles=new ArrayList<>();
            display();            
        }
        
        public void display(){
        VBox box=new VBox();
        box.setStyle("-fx-background-color: black;"); 
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10.0d);
        
        Button close,start;
        close=new Button("BACK TO MENU");
        start=new Button("START GAME");   
        
        Label title = new Label("STICK RUNNER");
        title.setAlignment(Pos.CENTER);
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Arial",45));
        
        close.setOnAction(e -> {stopGame();window.close();});
        box.getChildren().addAll(title,start,close);  
        if(played){
            Label disScore=new Label("Score:"+score);
            disScore.setTextFill(Color.WHITE);
            disScore.setFont(new Font("Arial",30));
            disScore.setAlignment(Pos.CENTER);
            box.getChildren().add(disScore);
        }
        
        start.setOnAction(e -> {startGame();played=true;});
        Scene scene=new Scene(box);
        window.setScene(scene);
        window.show();        
        }
        
        public void stopGame(){
            running=false;
            obstacles.clear();            
            t.getKeyFrames().clear();
            t.stop();  
            display();
        }   
        
        public void startGame(){
            score=0;
            ymotion=0;
            ticks=0;
            jumping=false;
            upJump=false;
            running=true;
            gameLoop();
        }
        
        public void gameLoop(){
            Pane root=new Pane();
            root.setPrefSize(width,height);
            root.setStyle("-fx-background-color: #FFFFFF");       
            Scene scene=new Scene(root);   
                    
            ground = new Line();   
            ground.setStartX(0);
            ground.setStartY(height-80);
            ground.setEndX(width);
            ground.setEndY(ground.getStartY());
            ground.setFill(Color.BLACK);
            root.getChildren().add(ground);
            
            for(int i=0;i<100;i++){
                addObstacle();
            }
            root.getChildren().addAll(obstacles);
            
            Image img=new Image("stickrun2.gif");
            sprite=new ImageView(img);
            sprite.setPreserveRatio(true);
            sprite.setFitWidth(img.getWidth());
            sprite.setFitHeight(img.getHeight());
            sprite.setTranslateX(window.getWidth()/2-10);
            sprite.setTranslateY(ground.getStartY()-sprite.getFitHeight());
            sprite.setStyle("-fx-background-color: white;");
            root.getChildren().add(sprite);
            
            scoreLabel=new Label("Score:"+score);
            scoreLabel.setTranslateX(0);
            scoreLabel.setTranslateY(0);
            scoreLabel.setFont(new Font("Arial",20));
            scoreLabel.setTextFill(Color.BLACK);            
            root.getChildren().add(scoreLabel);
            
            collCheck = new Rectangle();
            collCheck.setWidth(sprite.getFitWidth()-5);
            collCheck.setHeight(sprite.getFitHeight()-5);
            collCheck.setTranslateX(sprite.getTranslateX());
            collCheck.setTranslateY(sprite.getTranslateY());
            collCheck.setFill(Color.WHITE);
            
            KeyFrame game=new KeyFrame(Duration.seconds(0.04),e -> {
                 for(int i=0;i<obstacles.size();i++){
                     Rectangle obstacle=obstacles.get(i);
                     obstacle.setTranslateX(obstacle.getTranslateX()-5);
                     if(obstacle.getTranslateX()+obstacle.getWidth()<0){
                         obstacle.setTranslateX(obstacles.size()*200);                         
                     }
                 }
                 ticks++;
                 if(jumping)jump();
                 if(sprite.getTranslateY()+ymotion>ground.getStartY()-sprite.getFitHeight()){
                     ymotion=0;
                     sprite.setTranslateY(ground.getStartY()-sprite.getFitHeight());
                     collCheck.setTranslateY(sprite.getTranslateY());
                     jumping=false;
                     upJump=true;
                 }
                 if(sprite.getTranslateY()+ymotion<ground.getStartY()-70){
                     upJump=false;
                 }
                 sprite.setTranslateY(sprite.getTranslateY()+ymotion);   
                 collCheck.setTranslateY(sprite.getTranslateY());
                 scene.setOnKeyReleased(ev -> {
                     if(ev.getCode()==KeyCode.UP||ev.getCode()==KeyCode.W){
                         if(!jumping){jumping=true;upJump=true;}
                     }                    
                 }); 
                 collisionDetection();
            });
            
            t.setCycleCount(Animation.INDEFINITE);
            t.getKeyFrames().addAll(game);
            t.play();
            
            window.setScene(scene);            
        }
        
        public void jump(){           
           if(!upJump){ymotion+=1;}
           else {ymotion-=1;}
        }
        
        public void collisionDetection(){
            for(int i=0;i<obstacles.size();i++){
                Rectangle obstacle=obstacles.get(i);
                if(obstacle.getBoundsInParent().intersects(collCheck.getBoundsInParent())){
                    stopGame();
                }
                 if(sprite.getTranslateX()==obstacle.getTranslateX()){
                 score+=50;
                 scoreLabel.setText("Score:"+score);
             }
            }
            
        }
        
        public void addObstacle(){
            Random random=new Random();
            int obsHeight=random.nextInt(10)+15;
            
            Rectangle obstacle = new Rectangle(20,obsHeight);
            obstacle.setTranslateX(obstacles.size()*200+width+20);
            obstacle.setTranslateY(ground.getStartY()-obsHeight);
            obstacle.setFill(Color.BLACK);
            obstacle.setVisible(true);
            
            obstacles.add(obstacle);
        };
 
}
