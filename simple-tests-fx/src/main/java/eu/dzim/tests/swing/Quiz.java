package eu.dzim.tests.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Quiz extends JFrame {

    /**
     * Privates
     */
    private static final long serialVersionUID = 19641517864599359L;

    private final Executor swing = SwingUtilities::invokeLater;
    private final Executor async = Executors.newSingleThreadExecutor();
    private final Random r = new Random();

    private JPanel content;
    private int trueAnswer;
    private String question, answerA, answerB, answerC, answerD;
    private JTextField txtQuestion, txtAnswerA, txtAnswerB, txtAnswerC, txtAnswerD, txtSolution;

    private List<String> fileLines = null;
    // private List<String> fileLines = Arrays.asList("TEST1;a1;b1;c1;d1;1", "TEST2;a2;b2;c2;d2;2", "TEST3;a3;b3;c3;d3;3", "TEST4;a4;b4;c4;d4;4");

    /**
     * Create the frame.
     */
    public Quiz() {
        // do nothing or init some vars
    }

    /**
     * Launch the application.
     *
     * @throws IOException
     * @throws InterruptedException
     */

    public static void main(String[] args) throws IOException, InterruptedException {

        Quiz frame = new Quiz();
        frame.updateContentQuestion();

        EventQueue.invokeLater(() -> {
            try {
                frame.createQuizFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    protected void updateContentQuestion() {

        if (fileLines == null) {
            Path path = Paths.get("src/Quiz/database.csv");
            if (Files.isRegularFile(path)) {
                try {
                    fileLines = Files.readAllLines(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (fileLines == null || fileLines.isEmpty()) {
            System.err.println("No data!");
            return;
        }

        int randomLine = (int) (r.nextInt(fileLines.size()));
        String[] data = fileLines.get(randomLine).split(";");

        question = data[0];
        answerA = data[1];
        answerB = data[2];
        answerC = data[3];
        answerD = data[4];

        trueAnswer = Integer.parseInt(data[5]);
    }

    protected void createQuizFrame() {

        setTitle("Quizzer");
        setBounds(100, 100, 500, 308);
        content = new JPanel();
        content.setBorder(new EmptyBorder(9, 9, 9, 9));
        setContentPane(content);
        content.setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lbl_Ques = new JLabel("Frage:");
        lbl_Ques.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        lbl_Ques.setBounds(10, 10, 48, 20);
        content.add(lbl_Ques);

        txtQuestion = new JTextField(question);
        txtQuestion.setColumns(10);
        txtQuestion.setBounds(60, 8, 428, 28);
        txtQuestion.setEditable(false);
        content.add(txtQuestion);

        // Button A
        JButton btn_Ans_A = new JButton("Antwort A");
        btn_Ans_A.setBounds(10, 48, 114, 26);
        btn_Ans_A.addActionListener(e -> {
            if (1 == trueAnswer) {
                txtSolution.setText("Richtig");
            } else if (2 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, B wäre richtig gewesen.");
            } else if (3 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, C wäre richtig gewesen.");
            } else if (4 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, D wäre richtig gewesen.");
            }
        });
        content.add(btn_Ans_A);

        // Textausgabe Antwort A
        txtAnswerA = new JTextField(answerA);
        txtAnswerA.setColumns(10);
        txtAnswerA.setBounds(128, 46, 360, 28);
        txtAnswerA.setEditable(false);
        content.add(txtAnswerA);

        // Button B
        JButton btn_Ans_B = new JButton("Antwort B");
        btn_Ans_B.setBounds(10, 88, 114, 26);
        btn_Ans_B.addActionListener(e -> {
            if (2 == trueAnswer) {
                txtSolution.setText("Richtig");
            } else if (1 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, A wäre richtig gewesen.");
            } else if (3 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, C wäre richtig gewesen.");
            } else if (4 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, D wäre richtig gewesen.");
            }
        });
        content.add(btn_Ans_B);

        // Textausgabe Antwort B
        txtAnswerB = new JTextField(answerB);
        txtAnswerB.setColumns(10);
        txtAnswerB.setBounds(128, 86, 360, 28);
        txtAnswerB.setEditable(false);
        content.add(txtAnswerB);

        // Button C
        JButton btn_Ans_C = new JButton("Antwort C");
        btn_Ans_C.setBounds(10, 128, 114, 26);
        btn_Ans_C.addActionListener(e -> {
            if (3 == trueAnswer) {
                txtSolution.setText("Richtig");
            } else if (1 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, A wäre richtig gewesen.");
            } else if (2 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, B wäre richtig gewesen.");
            } else if (4 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, D wäre richtig gewesen.");
            }
        });
        content.add(btn_Ans_C);

        // Textausgabe Antwort C
        txtAnswerC = new JTextField(answerC);
        txtAnswerC.setColumns(10);
        txtAnswerC.setEditable(false);
        txtAnswerC.setBounds(128, 126, 360, 28);
        content.add(txtAnswerC);

        // Button D
        JButton btn_Ans_D = new JButton("Antwort D");
        btn_Ans_D.setBounds(10, 168, 114, 26);
        btn_Ans_D.addActionListener(e -> {
            if (4 == trueAnswer) {
                txtSolution.setText("Richtig");
            } else if (1 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, A wäre richtig gewesen.");
            } else if (2 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, B wäre richtig gewesen.");
            } else if (3 == trueAnswer) {
                txtSolution.setText("Falsche Antwort, C wäre richtig gewesen.");
            }
        });
        content.add(btn_Ans_D);

        // Textausgabe Antwort D
        txtAnswerD = new JTextField(answerD);
        txtAnswerD.setColumns(10);
        txtAnswerD.setBounds(128, 166, 360, 28);
        txtAnswerD.setEditable(false);
        content.add(txtAnswerD);

        // Lösungsausgabe
        txtSolution = new JTextField();
        txtSolution.setColumns(10);
        txtSolution.setBounds(16, 210, 472, 28);
        txtSolution.setEditable(false);
        txtSolution.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(txtSolution);

        JButton btn_Next = new JButton("Nächste Frage");
        btn_Next.addActionListener(e -> {
            CompletableFuture.runAsync(this::updateContentQuestion, async).thenRunAsync(() -> {
                txtQuestion.setText(question);
                txtAnswerA.setText(answerA);
                txtAnswerB.setText(answerB);
                txtAnswerC.setText(answerC);
                txtAnswerD.setText(answerD);
            }, swing);
        });
        btn_Next.setBounds(372, 248, 118, 28);
        content.add(btn_Next);
    }
}