package ui;

import exp.Expression;
import exp.Number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Graph
{
    Expression varExpression;
    ArrayList<Number> pointsY = new ArrayList<>();

    int width = 32;
    int height = 32;
    char[][] graph = new char[width][height];

    int graphMinX = -16;    int graphMaxY = 0;
    int graphMaxX = 15;     int graphMinY = 0;
    double graphScaleX = 1.0; double graphScaleY = 0;

    public Graph(Expression e) {
        varExpression = new Expression();
        varExpression.copy(e);
        initArray();
        populateArray();
    }

    public Graph(String s) {
        this( new Expression(s) );
    }

    private void initArray() {
        // All characters are spaces by default
        for (int r = 0; r<height; r++) {
            for (int c = 0; c<width; c++) {
                graph[r][c] = '_';
            }
        }

        // Sides
        for (int c = 0; c<width; c++) { graph[0][c] = '═'; } // Top side
        for (int r = 0; r<width; r++) { graph[r][0] = '║'; } // Left side
        for (int r = 0; r<width; r++) { graph[r][31] = '║'; } // Right side
        for (int c = 0; c<width; c++) { graph[31][c] = '═'; } // Bottom side

        // Corners
        graph[0][0] = '╔';
        graph[0][31] = '╗';
        graph[31][0] = '╚';
        graph[31][31] = '╝';

    }

    private void populateArray() {
        // Error if varExpression is somehow null
        if( varExpression==null ) {
            System.out.println("Graphing error: Expression to be graphed is null!");
            return;
        }

        // Create a list of data points
        // These will plotted on the graph later
        pointsY.clear();
        for (int x = -16; x<16; x++) {
            Expression expr = new Expression(varExpression);
            expr.plugIn('x', new Number(x));
            expr.evaluateValue(x);
            Number dataPoint = new Number(expr.toStringNormal());

            pointsY.add(dataPoint);
        }

        // Find the max and min values in the array.
        // This will be used to determine the scale and vertical translation of the graph
        Number min = new Number(pointsY.get(0));
        Number max = new Number(pointsY.get(0));
        for (int i = 0; i<pointsY.size(); i++) {
            if( pointsY.get(i).gt(max) ) {
                max.set(pointsY.get(i));
            }
            if( pointsY.get(i).lt(min) ) {
                min.set(pointsY.get(i));
            }
        }
        // Store max and min Y
        graphMinY = min.v().intValue();
        graphMaxY = max.v().intValue();

        // Find range and scale of graph
        BigDecimal range = max.v().subtract(min.v());               // Difference between min and max
        BigDecimal scaleY = BigDecimal.valueOf(16);                 // Distance from one 'square' in the graph to a vertically adjacent one
        if( range.compareTo(BigDecimal.ZERO)!=0 ) {
            scaleY = range.divide( BigDecimal.valueOf(height) );    // Y values to divide by in order to plot the graph
        }
        graphScaleY = scaleY.doubleValue();

        // "Plot" points within data array
        BigDecimal minScaledY = min.v().divide(scaleY, 100, RoundingMode.HALF_UP);
        for ( int i = 0; i<pointsY.size(); i++ ) {
            BigDecimal scaledY = pointsY.get(i).v().divide(scaleY, 100, RoundingMode.HALF_UP); // Scale every points' Y so that all points within f:[-min,max] will fit within the graph
            BigDecimal tScaledY = scaledY.subtract(minScaledY);
            int row = 32 - (tScaledY.intValue());                      // Find row of point y = 0 should be in the middle of the graph

            if( row==-1 ) { row = 0; }
            if( row==32 ) { row = 31; }

            graph[row][i] = '█';                                    // Plot point
        }
    }

    public void draw() {
        for(int y = 0; y<height; y++) {
            for(int x = 0; x<width; x++) {
                char c = graph[y][x];
                // Characters that are a corner
                if( "╗╝".contains(c+"") ) {
                    System.out.print("═"+c);
                // Characters not to print twice
                } else
                if( "╔╚║█".contains(c+"") ) {
                    System.out.print(" "+c);
                // Characters to print twice
                } else {
                    System.out.print(String.valueOf(c)+String.valueOf(c));
                }
            }
            System.out.println();
        }

        System.out.println("Graph Legend: ");
        System.out.println("[min-x, max-x] = ["+graphMinX+", "+graphMaxX+"]");
        System.out.println("[min-y, max-y] = ["+graphMinY+", "+graphMaxY+"]");
        System.out.println("Scale X = "+graphScaleX);
        System.out.println("Scale Y = "+graphScaleY);
    }
}

