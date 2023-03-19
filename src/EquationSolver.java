/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

public class EquationSolver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SolverFrame frame = new SolverFrame();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}


class SolverFrame extends JFrame {
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    public SolverFrame() {
        setTitle("Resolver Sistema de Ecuaciones");
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        TwoByTwoPanel twoByTwoPanel = new TwoByTwoPanel();
        ThreeByThreePanel threeByThreePanel = new ThreeByThreePanel();

        mainPanel.add(twoByTwoPanel, "2x2");
        mainPanel.add(threeByThreePanel, "3x3");
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        JButton twoByTwoButton = new JButton("Sistema 2x2");
        twoByTwoButton.addActionListener((ActionEvent e) -> {
            cardLayout.show(mainPanel, "2x2");
            resizeFrame();
        });
        buttonPanel.add(twoByTwoButton, gbc);

        gbc.gridx++;
        JButton threeByThreeButton = new JButton("Sistema 3x3");
        threeByThreeButton.addActionListener((ActionEvent e) -> {
            cardLayout.show(mainPanel, "3x3");
            resizeFrame();
        });
        buttonPanel.add(threeByThreeButton, gbc);

        add(buttonPanel, BorderLayout.NORTH);
    }

    public void resizeFrame() {
        Dimension preferredSize = mainPanel.getPreferredSize();
        int width = (int) preferredSize.getWidth() + getInsets().left + getInsets().right;
        int height = (int) preferredSize.getHeight() + getInsets().top + getInsets().bottom;
        setSize(width, height);
        setLocationRelativeTo(null); // Centrar en la pantalla
    }

}

class TwoByTwoPanel extends JPanel {
    private final JTextField[][] matrixInputs;
    private JLabel resultLabel;
    private JTextArea calculationDescription ;


    private double determinant2x2(double[][] matrix) {
        return (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]);
    }

    private double[][] inverseMatrix2x2(double[][] matrix) {
        double det = determinant2x2(matrix);
        if (det == 0) {
            return null;
        }

        double[][] inverse = new double[2][2];
        inverse[0][0] = matrix[1][1] / det;
        inverse[0][1] = -matrix[0][1] / det;
        inverse[1][0] = -matrix[1][0] / det;
        inverse[1][1] = matrix[0][0] / det;

        return inverse;
    }


    public TwoByTwoPanel() {
        setPreferredSize(new Dimension(320 , 180));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        matrixInputs = new JTextField[2][3];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                matrixInputs[i][j] = new JTextField(3);
                add(matrixInputs[i][j], gbc);
                gbc.gridx++;
            }
            gbc.gridx = 0;
            gbc.gridy++;
        }

        JButton solveButton = new JButton("Resolver");
        solveButton.addActionListener((ActionEvent e) -> {
            double[][] matrix = new double[2][3];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    matrix[i][j] = Double.parseDouble(matrixInputs[i][j].getText());
                }
            }

            String calculationDescriptionText = generateCalculationDescription2x2(matrix);
            double[] result = solveSystem(matrix);
            if (result != null) {
                resultLabel.setText("x = " + result[0] + ", y = " + result[1]);
                calculationDescription.setText(calculationDescriptionText);
            } else {
                resultLabel.setText("Sin solución o infinitas soluciones");
                calculationDescription.setText("No se pudo calcular el resultado.");
            }
            ((SolverFrame) SwingUtilities.getWindowAncestor(this)).resizeFrame();
        });

        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy++;
        add(solveButton, gbc);

        resultLabel = new JLabel(" ");
        gbc.gridy++;
        add(resultLabel, gbc);

        calculationDescription = new JTextArea(5, 30);
        calculationDescription.setEditable(false);
        calculationDescription.setLineWrap(true);
        calculationDescription.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(calculationDescription);
        gbc.gridy++;
        add(scrollPane, gbc);


    }

    private String generateCalculationDescription2x2(double[][] matrix) {
        StringBuilder description = new StringBuilder();

        description.append("Dado el sistema de ecuaciones:\n");
        description.append(matrix[0][0]).append("x + ").append(matrix[0][1]).append("y = ").append(matrix[0][2]).append("\n");
        description.append(matrix[1][0]).append("x + ").append(matrix[1][1]).append("y = ").append(matrix[1][2]).append("\n\n");

        description.append("Para resolver este sistema de ecuaciones utilizando el método de inversas multiplicativas, primero calculamos la matriz inversa de la matriz de coeficientes.\n\n");

        double det = determinant2x2(matrix);
        description.append("La determinante de la matriz de coeficientes es: ").append(det).append("\n\n");

        if (det == 0) {
            description.append("La matriz de coeficientes no tiene inversa, por lo que el sistema no tiene solución única o tiene infinitas soluciones.\n");
        } else {
            double[][] inverseMatrix = inverseMatrix2x2(matrix);
            description.append("La matriz inversa es:\n");
            assert inverseMatrix != null;
            description.append(inverseMatrix[0][0]).append("  ").append(inverseMatrix[0][1]).append("\n");
            description.append(inverseMatrix[1][0]).append("  ").append(inverseMatrix[1][1]).append("\n\n");

            double[] result = solveSystem(matrix);
            description.append("Multiplicamos la matriz inversa por el vector de constantes para obtener el vector de soluciones:\n");
            assert result != null;
            description.append("x = ").append(result[0]).append("\n");
            description.append("y = ").append(result[1]).append("\n");
        }

        return description.toString();
    }

    private double[] solveSystem(double[][] matrix) {
        double[][] coefficientMatrix = new double[][] {
                {
                        matrix[0][0], matrix[0][1]
                }, {
                matrix[1][0],
                matrix[1][1]
        }
        };
        double[] constants = new double[] {
                matrix[0][2], matrix[1][2]
        };

        double[][] inverseMatrix = inverseMatrix2x2(coefficientMatrix);
        if (inverseMatrix == null) {
            return null;
        }

        DecimalFormat df = new DecimalFormat("#.##");

        double[] result = new double[2];
        result[0] = Double.parseDouble(df.format(inverseMatrix[0][0] * constants[0] + inverseMatrix[0][1] * constants[1]));
        result[1] = Double.parseDouble(df.format(inverseMatrix[1][0] * constants[0] + inverseMatrix[1][1] * constants[1]));;

        return result;
    }

}

class ThreeByThreePanel extends JPanel {
    private final JTextField[][] matrixInputs;
    private JLabel resultLabel;
    private JTextArea calculationDescription;

    public ThreeByThreePanel() {

        setPreferredSize(new Dimension(480 , 320));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        matrixInputs = new JTextField[3][4];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                matrixInputs[i][j] = new JTextField(3);
                add(matrixInputs[i][j], gbc);
                gbc.gridx++;
            }
            gbc.gridx = 0;
            gbc.gridy++;
        }

        JButton solveButton = new JButton("Resolver");
        solveButton.addActionListener((ActionEvent e) -> {
            double[][] matrix = new double[3][4];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    matrix[i][j] = Double.parseDouble(matrixInputs[i][j].getText());
                }
            }
            double[] result = solveSystem(matrix);
            if (result != null) {
                resultLabel.setText("x = " + result[0] + ", y = " + result[1] + ", z = " + result[2]);
                String description = generateCalculationDescription3x3(matrix);
                calculationDescription.setText(description);
            } else {
                resultLabel.setText("Sin solución o infinitas soluciones");
                calculationDescription.setText("");
            }
            ((SolverFrame) SwingUtilities.getWindowAncestor(this)).resizeFrame();
        });


        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy++;
        add(solveButton, gbc);

        resultLabel = new JLabel(" ");
        gbc.gridy++;
        add(resultLabel, gbc);

        calculationDescription = new JTextArea(5, 30);
        calculationDescription.setEditable(false);
        calculationDescription.setLineWrap(true);
        calculationDescription.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(calculationDescription);
        gbc.gridy++;
        add(scrollPane, gbc);


    }
    private double determinant3x3(double[][] matrix) {
        return matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]) -
                matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0]) +
                matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
    }



    private double[][] inverseMatrix3x3(double[][] matrix) {
        double det = determinant3x3(matrix);
        if (det == 0) {
            return null;
        }

        double[][] inverse = new double[3][3];
        inverse[0][0] = (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]) / det;
        inverse[0][1] = (matrix[0][2] * matrix[2][1] - matrix[0][1] * matrix[2][2]) / det;
        inverse[0][2] = (matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1]) / det;
        inverse[1][0] = (matrix[1][2] * matrix[2][0] - matrix[1][0] * matrix[2][2]) / det;
        inverse[1][1] = (matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0]) / det;
        inverse[1][2] = (matrix[0][2] * matrix[1][0] - matrix[0][0] * matrix[1][2]) / det;
        inverse[2][0] = (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]) / det;
        inverse[2][1] = (matrix[0][1] * matrix[2][0] - matrix[0][0] * matrix[2][1]) / det;
        inverse[2][2] = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) / det;

        return inverse;
    }


    private double[] solveSystem(double[][] matrix) {
        double[][] coefficientMatrix = new double[][] {
                {
                        matrix[0][0], matrix[0][1], matrix[0][2]
                }, {
                matrix[1][0],
                matrix[1][1],
                matrix[1][2]
        }, {
                matrix[2][0],
                matrix[2][1],
                matrix[2][2]
        }
        };
        double[] constants = new double[] {
                matrix[0][3], matrix[1][3], matrix[2][3]
        };

        double[][] inverseMatrix = inverseMatrix3x3(coefficientMatrix);
        if (inverseMatrix == null) {
            return null;
        }
        DecimalFormat df = new DecimalFormat("#.##");

        double[] result = new double[3];
        result[0] = Double.parseDouble(df.format(inverseMatrix[0][0] * constants[0] + inverseMatrix[0][1] * constants[1] + inverseMatrix[0][2] * constants[2]));
        result[1] = Double.parseDouble(df.format(inverseMatrix[1][0] * constants[0] + inverseMatrix[1][1] * constants[1] + inverseMatrix[1][2] * constants[2]));
        result[2] = Double.parseDouble(df.format(inverseMatrix[2][0] * constants[0] + inverseMatrix[2][1] * constants[1] + inverseMatrix[2][2] * constants[2]));

        return result;
    }
    private String generateCalculationDescription3x3(double[][] matrix) {
        StringBuilder description = new StringBuilder();

        description.append("Dado el sistema de ecuaciones:\n");
        description.append(matrix[0][0]).append("x + ").append(matrix[0][1]).append("y + ").append(matrix[0][2]).append("z = ").append(matrix[0][3]).append("\n");
        description.append(matrix[1][0]).append("x + ").append(matrix[1][1]).append("y + ").append(matrix[1][2]).append("z = ").append(matrix[1][3]).append("\n");
        description.append(matrix[2][0]).append("x + ").append(matrix[2][1]).append("y + ").append(matrix[2][2]).append("z = ").append(matrix[2][3]).append("\n\n");

        description.append("Para resolver este sistema de ecuaciones utilizando el método de inversas multiplicativas, primero calculamos la matriz inversa de la matriz de coeficientes.\n\n");

        double det = determinant3x3(matrix);
        description.append("La determinante de la matriz de coeficientes es: ").append(String.format("%.3f", det)).append("\n\n");

        if (det == 0) {
            description.append("La matriz de coeficientes no tiene inversa, por lo que el sistema no tiene solución única o tiene infinitas soluciones.\n");
        } else {



            description.append("Para calcular la matriz inversa, seguimos los siguientes pasos:\n\n");

            description.append("1. Calculamos la matriz de cofactores:\n");
            double[][] cofactorMatrix = cofactorMatrix3x3(matrix);
            description.append(String.format("%.3f", cofactorMatrix[0][0])).append("  ").append(String.format("%.3f", cofactorMatrix[0][1])).append("  ").append(String.format("%.3f", cofactorMatrix[0][2])).append("\n");
            description.append(String.format("%.3f", cofactorMatrix[1][0])).append("  ").append(String.format("%.3f", cofactorMatrix[1][1])).append("  ").append(String.format("%.3f", cofactorMatrix[1][2])).append("\n");
            description.append(String.format("%.3f", cofactorMatrix[2][0])).append("  ").append(String.format("%.3f", cofactorMatrix[2][1])).append("  ").append(String.format("%.3f", cofactorMatrix[2][2])).append("\n\n");



            description.append("2. Transponemos la matriz de cofactores para obtener la matriz adjunta:\n");
            double[][] adjugateMatrix = transposeMatrix(cofactorMatrix);
            description.append(String.format("%.3f", adjugateMatrix[0][0])).append("  ").append(String.format("%.3f", adjugateMatrix[0][1])).append("  ").append(String.format("%.3f", adjugateMatrix[0][2])).append("\n");
            description.append(String.format("%.3f", adjugateMatrix[1][0])).append("  ").append(String.format("%.3f", adjugateMatrix[1][1])).append("  ").append(String.format("%.3f", adjugateMatrix[1][2])).append("\n");
            description.append(String.format("%.3f", adjugateMatrix[2][0])).append("  ").append(String.format("%.3f", adjugateMatrix[2][1])).append("  ").append(String.format("%.3f", adjugateMatrix[2][2])).append("\n\n");

            description.append("3. Calculamos la inversa dividiendo la matriz adjunta por la determinante:\n");

            double[][] inverseMatrix = divideMatrix(adjugateMatrix, det);
            description.append(String.format("%.3f", inverseMatrix[0][0])).append("  ").append(String.format("%.3f", inverseMatrix[0][1])).append("  ").append(String.format("%.3f", inverseMatrix[0][2])).append("\n");
            description.append(String.format("%.3f", inverseMatrix[1][0])).append("  ").append(String.format("%.3f", inverseMatrix[1][1])).append("  ").append(String.format("%.3f", inverseMatrix[1][2])).append("\n");
            description.append(String.format("%.3f", inverseMatrix[2][0])).append("  ").append(String.format("%.3f", inverseMatrix[2][1])).append("  ").append(String.format("%.3f", inverseMatrix[2][2])).append("\n\n");

            double[] result = solveSystem(matrix);
            description.append("Multiplicamos la matriz inversa por el vector de constantes para obtener el vector de soluciones:\n");
            assert result != null;
            description.append("x = ").append(String.format("%.3f", result[0])).append("\n");
            description.append("y = ").append(String.format("%.3f", result[1])).append("\n");
            description.append("z = ").append(String.format("%.3f", result[2])).append("\n");
        }

        return description.toString();

    }
    private double[][] cofactorMatrix3x3(double[][] matrix) {
        double[][] cofactorMatrix = new double[3][3];

        cofactorMatrix[0][0] = matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1];
        cofactorMatrix[0][1] = -1 * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0]);
        cofactorMatrix[0][2] = matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0];

        cofactorMatrix[1][0] = -1 * (matrix[0][1] * matrix[2][2] - matrix[0][2] * matrix[2][1]);
        cofactorMatrix[1][1] = matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0];
        cofactorMatrix[1][2] = -1 * (matrix[0][0] * matrix[2][1] - matrix[0][1] * matrix[2][0]);

        cofactorMatrix[2][0] = matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1];
        cofactorMatrix[2][1] = -1 * (matrix[0][0] * matrix[1][2] - matrix[0][2] * matrix[1][0]);
        cofactorMatrix[2][2] = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        return cofactorMatrix;
    }

    public static double[][] transposeMatrix(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        double[][] transposedMatrix = new double[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }

        return transposedMatrix;
    }

    private double[][] divideMatrix(double[][] matrix, double scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] / scalar;
            }
        }

        return result;
    }


}