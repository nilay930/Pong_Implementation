package cs1302.omega;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

/**
 * A pong game where a user plays against an AI.
 */
public class OmegaApp extends Application {
    // Moving Obj Sizes
    private int paddleWidth = 15;
    private int paddleHeight = 100;
    private int ball = 15;
    // Background Graphic Sizes
    private int width = 750;
    private int height = 500;
    private GraphicsContext gc;
    private Canvas canvas;
    // Player Stuff
    private int p1PosX = 0;
    private int p2PosX = width - paddleWidth;
    private double p1PosY = height / 2 - (paddleHeight / 2); // starting height
    private double p2PosY = height / 2 - (paddleHeight / 2);
    private int p1Score = 0;
    private int p2Score = 0;
    // Ball Stuff
    private double ballSize = 10;
    private double ballVelX = 1.3;
    private double ballVelY = 1.3;
    private double ballPosX = width / 2 - (ballSize / 2);
    private double ballPosY = height / 2 - (ballSize / 2);
    // idrk
    private final Timeline loop = new Timeline();
    private boolean playing = false;
    private boolean startgame = false;

    /**
     * Constructs an {@code OmegaApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public OmegaApp() {}

    /**
     * Creates the canvas for the elements to be added to the game.
     * @return canvas a canvas with the starter screen
     */
    public Canvas createBoard() {
        canvas = new Canvas(width, height);
        canvas.setFocusTraversable(true);
        // Background
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,width,height);
        // Paddles, Ball, Instructions
        gc.setFill(Color.WHITE);
        gc.fillText("PONG!", width / 2 - 14, height * 0.25);
        gc.fillText("First to 3 wins!", width / 2 - 34, height * 0.35);
        gc.fillText("Control the paddle using your cursor.", width / 2 - 88, height * 0.45);
        gc.fillRect(p1PosX, p1PosY, paddleWidth, paddleHeight);
        gc.fillRect(p2PosX, p2PosY, paddleWidth, paddleHeight);
        return canvas;
    } // createBoard

    /**
     * Helper method to update the canvas with new positions of graphics.
     */
    public void updateBoard() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,width,height);
        // Paddles and Ball
        gc.setFill(Color.WHITE);
        gc.fillRect(p1PosX, p1PosY, paddleWidth, paddleHeight);
        gc.fillRect(p2PosX, p2PosY, paddleWidth, paddleHeight);
        gc.setFill(Color.WHITE);
        gc.fillOval(ballPosX, ballPosY, ball, ball);
        gc.fillText(String.valueOf(p1Score), width * 0.25, height * 0.25);
        gc.fillText(String.valueOf(p2Score), width * 0.75, height * 0.25);
    }

    /**
     * Movement for the ball and paddle control and manages the other helper methods.
     */
    public void movement() {
        if (playing == true) {
            controller();
            oppPaddle();
            ballPosX = ballPosX + ballVelX;
            ballPosY = ballPosY + ballVelY;
            scoring();
            trajection();
            updateBoard();
            if (p1Score == 2 || p2Score == 2) {
                startgame = false;
            }
        } else {
            if (p1Score == 3) {
                gc.fillText("You won!", width / 2, height * 0.25);
            }
            if (p2Score == 3) {
                gc.fillText("You lost to a bot.", width / 2, height * 0.25);
            }
            if (p1Score == 3 || p2Score == 3) {
                gc.fillText("Press 'Space' to play again!", 300, height / 2 + 150);
                canvas.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                    if (e.getCode() == KeyCode.SPACE) {
                        p1Score = 0;
                        p2Score = 0;
                        ballVelX = 1.3;
                        ballVelY = 1.3;
                        playing = true;
                    }
                }); // end lambda
            } else {
                gc.fillText("Press 'Space' to continue!", 300, height / 2 + 150);
                canvas.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                    if (e.getCode() == KeyCode.SPACE) {
                        ballVelX = 1.3;
                        ballVelY = 1.3;
                        playing = true;
                    }
                }); // end lambda
            }
        } // if playing is true
    } // movement

    /**
     * Helper method that detects scores and increments them.
     */
    public void scoring() {
        if (ballPosX < p1PosX) {
            p2Score++;
            ballPosX = width / 2;
            ballPosY = height / 2;
            playing = false;
            // System.out.println("P2 Scores: " + p2Score);
        }
        if (ballPosX > p2PosX + paddleWidth) {
            p1Score++;
            ballPosX = width / 2;
            ballPosY = height / 2;
            playing = false;
            // System.out.println("P1 Scores: " + p1Score);
        }
    } // scoring

    /**
     * Helper method to calculate trajection of the ball and
     * the randomness of the AI missing the ball.
     */
    public void trajection() {
        if (ballPosY > height || ballPosY < 0) {
            ballVelY = ballVelY + ((Math.random() * (0.1)) + 0.1) * Math.signum(ballVelY);
            ballVelY = ballVelY * -1;
        } // if for ceiling bounces
        if (ballPosX <= (p1PosX + paddleWidth) &&
            (ballPosY >= p1PosY) && (ballPosY <= p1PosY + paddleHeight)) {
            ballVelX = ballVelX + ((Math.random() * (0.3)) + 0.8) * Math.signum(ballVelX);
            ballVelX = ballVelX * -1;
            System.out.println(ballVelX);
        } // if for player 1 ball bouncing
        if ((ballPosX + ball > p2PosX) &&
                (ballPosY >= p2PosY) && (ballPosY <= p2PosY + paddleHeight)) {
            // this section determines if the AI will miss the ball
            int key1 = (int) (Math.random() * 7 + 1);
            int key2 = 3;
            System.out.println("Key 1: " + key1 + " || " + key2);
            if (ballVelX > 6 && (key1 == key2)) {
                p2PosY += 200;
                System.out.println("miss! -> point player 1");
                p1Score++;
                ballPosX = width / 2;
                ballPosY = height / 2;
                playing = false;
            } else {
                ballVelX = ballVelX * -1;
            } // if else
        } // if for player 2 ball bouncing
    } // trajection

    /**
     * Helper method that allows the user to control their paddle.
     */
    public void controller() {
        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    p1PosY = e.getY();
                    // p2PosY = e.getY();
                }
            });
    } // controller for the player paddle

    /**
     * Helper method that tracks the ball for the AI to play.
     */
    public void oppPaddle() {
        p2PosY = ballPosY - paddleHeight / 2;
        // p1PosY = ballPosY - paddleHeight / 2;
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(10), e -> movement()));
        timeline2.setCycleCount(Timeline.INDEFINITE);
        VBox root = new VBox();
        root.getChildren().addAll(createBoard());
        Scene scene = new Scene(root);
        // setup stage
        stage.setMaxWidth(1280);
        stage.setMaxHeight(720);
        stage.setTitle("OmegaApp - Pong! ");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
        timeline2.play();
        // play the game
        // game.play();
    } // start

    /**
    private void initGameLoop() {
        KeyFrame updateFrame = new Keyframe(60, event -> {
            movement();
        });
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.getKeyFrames().add(updateFrame);
    } // initGameLoop
    */

} // OmegaApp
