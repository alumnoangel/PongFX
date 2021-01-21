package es.angelhuesovecina.pongfx;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {
    int ballCenterX = 10;
    int ballCurrentSpeedX = 3;
    int ballCenterY = 30;
    int ballCurrentSpeedY = 3;
    final int SCENE_TAM_X = 600;
    final int SCENE_TAM_Y = 400;
    final int STICK_WIDTH = 7;
    final int STICK_HEIGHT = 50;
    int stickPosY = (SCENE_TAM_Y - STICK_HEIGHT) / 2;
    int stickCurrentSpeed = 0;
    final int TEXT_SIZE = 24;
    //Puntuacion actual
    int score;
   //Puntuacion maxima
    int highScore;
    Text textScore;
    Pane root;
    int collisionZone;
    
    @Override
    public void start(Stage stage) {
        root = new Pane();
        var scene = new Scene(root, SCENE_TAM_X, SCENE_TAM_Y);
        scene.setFill(Color.BLACK);
        stage.setTitle("PongFX");
        stage.setScene(scene);
        stage.show();

        Circle circleBall = new Circle();
        circleBall.setCenterX(ballCenterX);
        circleBall.setCenterY(ballCenterY);
        circleBall.setRadius(7);
        circleBall.setFill(Color.WHITE);
        root.getChildren().add(circleBall);
        
        Rectangle rectStick = new Rectangle (SCENE_TAM_X*0.9, stickPosY,
                STICK_WIDTH, STICK_HEIGHT);
        rectStick.setFill(Color.WHITE);
        root.getChildren().add(rectStick);
        
        
       //Creacion de la red
        drawNet(10, 4, 30);
        
        //LAYOUTS PARA MOSTRAR PUNTUACIONES
        //Layout principal
        HBox paneScores = new HBox ();
        paneScores.setTranslateY(20);
        paneScores.setMinWidth(SCENE_TAM_X);
        paneScores.setAlignment(Pos.CENTER);
        paneScores.setSpacing(100);
        root.getChildren().add(paneScores);
        //Layout para puntuacion actual
        HBox paneCurrentScore = new HBox();
        paneCurrentScore.setSpacing(10);
        paneScores.getChildren().add(paneCurrentScore);
        //Layout para puntuacion maxima
        HBox paneHighScore = new HBox ();
        paneHighScore.setSpacing(10);
        paneScores.getChildren().add(paneHighScore);
        //Texto de etiqueta para la puntuacion
        Text textTitleScore = new Text ("Score");
        textTitleScore.setFont(Font.font(TEXT_SIZE));
        textTitleScore.setFill(Color.WHITE);
        //Texto para la puntuacion
        textScore = new Text ("0");
        textScore.setFont(Font.font(TEXT_SIZE));
        textScore.setFill(Color.WHITE);
        //Texto de etiqueta para la puntuacion maxima
        Text textTitleHighScore = new Text ("Max.Score");
        textTitleHighScore.setFont(Font.font(TEXT_SIZE));
        textTitleHighScore.setFill(Color.WHITE);
        //Texto para la puntuacion maxima
        Text textHighScore = new Text ("0");
        textHighScore.setFont(Font.font(TEXT_SIZE));
        textHighScore.setFill(Color.WHITE);
        //Añadir los textos a los layouts reservador para ellos
        paneCurrentScore.getChildren().add(textTitleScore);
        paneCurrentScore.getChildren().add(textScore);
        paneHighScore.getChildren().add(textTitleHighScore);
        paneHighScore.getChildren().add(textHighScore);
        
        resetGame();

        Timeline animationBall = new Timeline(
                new KeyFrame(Duration.seconds(0.017), (ActionEvent ae) -> {
                    circleBall.setCenterX(ballCenterX);
                    ballCenterX+=ballCurrentSpeedX;
                    if (ballCenterX >= SCENE_TAM_X) {
                        if (score > highScore){
                            highScore = score;
                            textHighScore.setText(String.valueOf(highScore));
                        }
                        //Reiniciar partida
                        resetGame();
                    }
                    if (ballCenterX <= 0) {
                        ballCurrentSpeedX = 3;
                    }
                    circleBall.setCenterY(ballCenterY);
                    ballCenterY += ballCurrentSpeedY;
                    if (ballCenterY >= SCENE_TAM_Y) {
                        ballCurrentSpeedY = -3;
                    }
                    if (ballCenterY <= 0) {
                        ballCurrentSpeedY = 3;
                    }
                    //Actualizar animacion de la pala
                    stickPosY += stickCurrentSpeed;
                    if (stickPosY < 0){
                        //No sobrepasar el borde superior de la pantalla
                        stickPosY = 0;
                    } else {
                        //No sobrepasar el borde inferior de la pantalla
                        if(stickPosY > SCENE_TAM_Y - STICK_HEIGHT){
                            stickPosY = SCENE_TAM_Y - STICK_HEIGHT;
                        }
                    }
                    
                    Shape shapeColision = Shape.intersect(circleBall, rectStick);
                    boolean colisionVacia = shapeColision.getBoundsInLocal().isEmpty();
                    if (colisionVacia == false && ballCurrentSpeedX > 0){
                        ballCurrentSpeedX = -3;
                        //Incrementar puntuacion actual
                        score++;
                        textScore.setText(String.valueOf(score));
                    }
                    
                    rectStick.setY(stickPosY);
                    
                    
                    
                }));
        
        scene.setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                case UP:
                //Pulsada tecla arrriba
                stickCurrentSpeed = -6;
                break;
                case DOWN:
                //Pulsada tecla abajo
                stickCurrentSpeed = 6;
                break;
             }
            
        });
        
        scene.setOnKeyReleased((KeyEvent event) -> {
            stickCurrentSpeed = 0;
        });
        
        animationBall.setCycleCount(Timeline.INDEFINITE);
        animationBall.play();
        
        Shape.intersect(circleBall, rectStick);
        
        //Zona colision
        collisionZone = getStickCollisionZone (circleBall, rectStick);
        
        //Cambiar velocidad de la bola
        calculateBallSpeed (collisionZone);

    }
    
    private void resetGame(){
    //Reiniciar partida
    score = 0;
    textScore.setText(String.valueOf(score));
    ballCenterX = 10;
    ballCurrentSpeedY = 3;
    //Posicion inicial aleatoria para la bola en eje Y
    Random random = new Random ();
    ballCenterY = random.nextInt(SCENE_TAM_Y);
    }

    private void drawNet (int portionHeight, int portionWidth, int portionSpacing){
        for (int i=0; i<SCENE_TAM_Y; i+=portionSpacing){
            Line line = new Line (SCENE_TAM_X/2, i, SCENE_TAM_X/2, i+portionHeight);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(portionWidth);
            root.getChildren().add(line);
        }
    }
    
    private int getStickCollisionZone (Circle ball, Rectangle stick){
        if (Shape.intersect(ball, stick).getBoundsInLocal().isEmpty()){
            return 0;
        } else {
            double offsetBallStick = ball.getCenterY() - stick.getY();
            if (offsetBallStick < stick.getHeight()*0.1){
                return 1;
            } else if(offsetBallStick < stick.getHeight()/2){
                return 2;
            }else if(offsetBallStick >= stick.getHeight()/2 &&
                    offsetBallStick < stick.getHeight()*0.9){
                return 3;
            }else{
                return 4;
            }
        }
    }
    
    private void calculateBallSpeed (int CollisionZone){
        switch (collisionZone){
            case 0:
                //No hay colision
                break;
            case 1:
                //Hay colision esquina superior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = -6;
                break;
            case 2:
                //Hay colision lado superior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = -3;
                break;
            case 3:
                //Hay colision lado inferior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = 3;
                break;
            case 4:
                //Hay colision esquina inferior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = 6;
                break;
        }
    }

    public static void main(String[] args) {
        launch();
        
    }

}
