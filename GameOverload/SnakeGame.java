import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SnakeGame {
    
    private final Stage window;
    private ObservableList<Node> snakeBody;
    private boolean running = false;
    private double speed;
    private Direction dir;
    private Label scoreLabel;
    private Group snakeGroup;
    private Timeline t;
    private int score=0;
    private final int blocksize=20;
    private final int height=15*blocksize;
    private final int width=15*blocksize;
    private boolean played=false;
    
    public SnakeGame(){        
        window=new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setHeight(height);
        window.setWidth(width);
        window.setMinHeight(height);
        window.setMinWidth(width);
        window.setMaxWidth(width);
        window.setMaxHeight(height);
        window.setResizable(false);
        window.initStyle(StageStyle.UNDECORATED);
        window.setTitle("SNAKE CLASSIC");
        snakeBody=FXCollections.observableArrayList();
        t=new Timeline();
        snakeGroup=new Group();
        display();
    }
    
    public void display(){        
        VBox box=new VBox();
        box.setStyle("-fx-background-color: white;");
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10.0d);
        
        Button close,start;
        close=new Button("BACK TO MENU");
        start=new Button("START GAME"); 
        
        Label title = new Label("SNAKE CLASSIC");
        title.setAlignment(Pos.CENTER);
        title.setTextFill(Color.BLACK);
        title.setFont(new Font("Arial",20));
        
        ComboBox<String> dif = new ComboBox<>();
        dif.setPromptText("Choose a difficluty");
        dif.getItems().addAll("EASY","MEDIUM","HARD");
        
        close.setOnAction(e -> {stopGame();window.close();});
        start.setOnAction(e -> {
            played=true;
            switch(dif.getSelectionModel().getSelectedIndex()){
                case 0:{speed=0.2;startGame();break;}
                case 1:{speed=0.07;startGame();break;}
                case 2:{speed=0.03;startGame();break;}
                default:{dif.setPromptText("Please choose difficulty");}
            }
    });
       box.getChildren().addAll(title,dif,start,close);   
       if(played){
            Label disScore=new Label("Score:"+score);
            disScore.setTextFill(Color.BLACK);
            disScore.setFont(new Font("Arial",30));
            disScore.setAlignment(Pos.CENTER);
            box.getChildren().add(disScore);
       }
       Scene scene=new Scene(box);
       window.setScene(scene);
       window.show();
    }
    
    public void startGame(){
        running=true;
        snakeBody.clear();  
        score=0;
        gameLoop();
    }
    
    public void gameLoop(){
        Pane root=new Pane();
        root.setPrefSize(width,height);
        root.setStyle("-fx-background-color: white;");
        dir=Direction.RIGHT;
        Scene scene=new Scene(root);
        
        scoreLabel=new Label(String.valueOf(score));  
        
        snakeBody=snakeGroup.getChildren();
        Rectangle rect;
        
        Lighting eff=new Lighting();
        eff.setDiffuseConstant(1.0);
        eff.setSurfaceScale(1.0d);
        
        for(int i=0,j=0;i<=1;i++,j+=blocksize){
        rect=new Rectangle(blocksize,blocksize);
        rect.setFill(Color.WHITE);
        rect.setTranslateX(j);
        rect.setTranslateY(0);
        rect.setEffect(eff);
        
        snakeBody.add(rect); 
        }
        
        
        Random random=new Random();
        
        Rectangle fruit=new Rectangle(blocksize,blocksize);
        fruit.setFill(Color.BLUE);
        fruit.setTranslateX(random.nextInt((width-2*blocksize)/blocksize)*blocksize);
        fruit.setTranslateY(random.nextInt((width-2*blocksize)/blocksize)*blocksize);
        
        scene.setOnKeyPressed(e -> {
            
            switch(e.getCode()){
                case UP:
                case W:if(dir!=Direction.DOWN)dir=Direction.UP;break;
                case DOWN:
                case S:if(dir!=Direction.UP)dir=Direction.DOWN;break;
                case RIGHT:
                case D:if(dir!=Direction.LEFT)dir=Direction.RIGHT;break;
                case LEFT:
                case A:if(dir!=Direction.RIGHT)dir=Direction.LEFT;break;
                case ESCAPE:stopGame();
                }
            
        });
        
        KeyFrame game=new KeyFrame(Duration.seconds(speed), e -> {
             if(!running)return;
             boolean check=snakeBody.size()>1;
             Rectangle head=(Rectangle)(check?snakeBody.remove(snakeBody.size()-1):snakeBody.get(0));
             double headX=head.getTranslateX();
             double headY=head.getTranslateY();
             
             switch(dir){                 
                 case UP:{
                     head.setTranslateX(snakeBody.get(0).getTranslateX());
                     head.setTranslateY(snakeBody.get(0).getTranslateY()-blocksize);
                     break;
                 }case DOWN:{
                     head.setTranslateX(snakeBody.get(0).getTranslateX());
                     head.setTranslateY(snakeBody.get(0).getTranslateY()+blocksize);                     
                     break;
                 }case RIGHT:{
                     head.setTranslateX(snakeBody.get(0).getTranslateX()+blocksize);
                     head.setTranslateY(snakeBody.get(0).getTranslateY());                     
                     break;
                 }case LEFT:{
                     head.setTranslateX(snakeBody.get(0).getTranslateX()-blocksize);
                     head.setTranslateY(snakeBody.get(0).getTranslateY());
                     break;
                 }
             }
             for(int i=0;i<snakeBody.size();i++){
                 Rectangle rectangle=(Rectangle)(snakeBody.get(i));
                 rectangle.setFill(Color.WHITE);
             }
             if(check)snakeBody.add(0,head);  
             head.setFill(Color.CHOCOLATE);
             //collision detection with snake
             for(int i=0;i<snakeBody.size();i++){
                if(snakeBody.get(i)!=head&&snakeBody.get(i).getTranslateX()==head.getTranslateX()&&snakeBody.get(i).getTranslateY()==head.getTranslateY()){
                    stopGame();                    
                }
             }
             //collision detection with fruit
             if(head.getTranslateX()==fruit.getTranslateX()&&head.getTranslateY()==fruit.getTranslateY()){
                 setScore(score+500);
                 scoreLabel.setText(String.valueOf(score));
                 for(int i=0;i<snakeBody.size();i++){
                      Rectangle rectangle = (Rectangle)snakeBody.get(i);
                      if(rectangle.getTranslateX()==fruit.getTranslateX()){                          
                        fruit.setTranslateX(random.nextInt((width-2*blocksize)/blocksize)*blocksize);
                        fruit.setTranslateY(random.nextInt((width-2*blocksize)/blocksize)*blocksize);
                        i=0;
                      }
                 }
                 Rectangle snakeNode=new Rectangle(blocksize,blocksize);
                 snakeNode.setTranslateX(headX);
                 snakeNode.setTranslateY(headY);
                 snakeNode.setFill(Color.WHITE);
                 snakeNode.setEffect(eff);
                 snakeBody.add(snakeNode);
             }
             //collision detection with window
             for(Node n:snakeBody){
                 if(n.getTranslateX()<0)n.setTranslateX(width-blocksize);
                 else if(n.getTranslateX()+blocksize>width)n.setTranslateX(0);
                 else if(n.getTranslateY()<0)n.setTranslateY(height-blocksize);
                 else if(n.getTranslateY()+blocksize>height)n.setTranslateY(0);
             }
        }
                
        );
        scoreLabel.setStyle("-fx-background-color: white;");
        scoreLabel.setTextFill(Color.RED);
        scoreLabel.setBackground(Background.EMPTY);
        scoreLabel.setFont(new Font("Arial",15));
        scoreLabel.setPrefSize(50,10);
        scoreLabel.setLayoutX(0);
        scoreLabel.setLayoutY(0);
        
        t.getKeyFrames().add(game);
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
            
        root.getChildren().addAll(scoreLabel,fruit,snakeGroup);
        window.setScene(scene);
    }
    
    public void setScore(int score){
        this.score=score;
    }
    
    public void stopGame(){
        if(!running)return;
        running=false;
        t.stop();
        t.getKeyFrames().clear();
        speed=0.2;
        display();
    }
    
    public enum Direction{
        UP,DOWN,LEFT,RIGHT
    }
    
}
