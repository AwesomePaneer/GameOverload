import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FlappyBird{

    private Stage window;
    private boolean running = false;    
    private int score=0;
    private Label scoreLabel;
    private final int height=700;
    private final int width=800;
    private ImageView bird;
    private ArrayList<Rectangle> columns;
    private Rectangle ground;
    private Timeline t;
    private int ticks=0;
    private int ymotion=0;
    private int y=0;
    private boolean played=false;
    private Lighting effect;
    
     public FlappyBird(){
        window=new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setHeight(height);
        window.setWidth(width);
        window.setMinHeight(height);
        window.setMinWidth(width);
        window.setMaxWidth(width);
        window.setMaxHeight(height);
        window.setResizable(false);
        window.setTitle("Flappy Bird");          
        columns=new ArrayList<>();
        t=new Timeline();
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
        
        Label title = new Label("FLAPPY BIRD");
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
        
        start.setOnAction(e -> startGame());
        Scene scene=new Scene(box);
        window.setScene(scene);
        window.show();
     }
     
     public void startGame(){
         running=true;
         score=0;
         ymotion=0;
         ticks=0;
         gameLoop();
     }
     
     public void gameLoop(){
       Pane root=new Pane();
       root.setPrefSize(width,height);
       root.setStyle("-fx-background-color: #00FFFF");       
       Scene scene=new Scene(root);
       
       effect=new Lighting();
       
       Image img=new Image("bird.png");
       bird=new ImageView(img);
       bird.setTranslateX(width/2-10);
       bird.setTranslateY(height/2-10);
       bird.setPreserveRatio(true);
       bird.setFitWidth(img.getWidth()+20);
       bird.setFitHeight(img.getHeight()+10);
       
       ground=new Rectangle(0,height-80,width,80);
       ground.setFill(Color.LIMEGREEN);     
       ground.setEffect(effect);
       t.setCycleCount(Animation.INDEFINITE);
       
       root.getChildren().addAll(ground,bird);
       
       scoreLabel = new Label(String.valueOf(score));
       scoreLabel.setText("Score:"+score);
       scoreLabel.setTextFill(Color.RED);
       scoreLabel.setFont(new Font("Arial",30));
       scoreLabel.setTranslateY(height-80);
       
       Label startLabel=new Label("Press UP or W to start");
       startLabel.setFont(new Font("Arial",50));       
       startLabel.setPrefSize(width,30);
       startLabel.setTranslateY(height/2+bird.getFitHeight());
       startLabel.setAlignment(Pos.CENTER);
       startLabel.setTextFill(Color.RED);
       root.getChildren().addAll(scoreLabel,startLabel);       
       
       t.pause();
       
       for(int i=0;i<25;i++)addColumn();
       
       scene.setOnKeyReleased(e -> {
           if(e.getCode()==KeyCode.UP||e.getCode()==KeyCode.W){
               root.getChildren().remove(startLabel);
               root.getChildren().addAll(columns);    
               played=true;
               t.play();
           }
       });
       
       KeyFrame game=new KeyFrame(Duration.millis(40),e -> {
              ticks++;
              if(ticks%2==0&&ymotion<15){   
                  ymotion+=2;
              }
              y=(int)bird.getTranslateY()+ymotion;
              bird.setTranslateY(y);
              
              scene.setOnKeyReleased(ev -> {
                  switch(ev.getCode()){
                      case UP:
                      case W:jump();break;
                      case ESCAPE:stopGame();
                  }
              });              
              
              
              
              collisionDetection();
       });
       
       KeyFrame columnKeyFrame = new KeyFrame(Duration.millis(40),e -> {
           
            for(int i=0;i<columns.size();i++){
                Rectangle column=columns.get(i);
                column.setFill(Color.BLACK);
                column.setX(column.getX()-5);
                column.setVisible(true);
                if(column.getX()+column.getWidth()<0){
                    column.setTranslateX(columns.size()*200);
                }
            }
       });
       
       t.getKeyFrames().addAll(game,columnKeyFrame);
       window.setScene(scene);
     }
     
     public void jump(){
         if(ymotion>0){
             ymotion=0;
         }
         ymotion-=12;
     }
     
     public void collisionDetection(){
         for(int i=0;i<columns.size();i++){
             Rectangle column=columns.get(i);
             //collision detection with columns
             if(column.getBoundsInParent().intersects(bird.getBoundsInParent())){
                 stopGame();
             }
             //adding score if not collided
             if(column.getY()==0&&bird.getTranslateX()+bird.getFitWidth()==column.getX()+column.getWidth()/2){
                 score+=50;
                 scoreLabel.setText("Score:"+score);
             }             
         }
         //collision detection with window
        if(bird.getTranslateY()+bird.getFitHeight()>height-80||bird.getTranslateY()<0){
            stopGame();
        }
     }
     
     public void addColumn(){
         int space=240;
         int colWidth=100;
         int colHeight=50+(int)(Math.random()*300);
         
         columns.add(new Rectangle(width+colWidth+columns.size()*200,height-colHeight-80,colWidth,colHeight));
         columns.add(new Rectangle(width+colWidth+(columns.size()-1)*200,0,colWidth,height-colHeight-space));
         
     }
     
     
     
     public void stopGame(){
         running=false;
         columns.clear();
         t.stop();  
         t.getKeyFrames().clear();
         display();
     }
    
}
