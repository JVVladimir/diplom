package ru.hse.graphic;



import java.util.ArrayList;

public class LineGraph {}/*extends Application {

    private static ArrayList<Double> outputA;
    private static ArrayList<Double> outputB;

    private final static String PATH_TO_CSS = "style.css";

    public void plot(ArrayList<Double> line1, ArrayList<Double> line2) {
        if (line1 == null || line2 == null)
            throw new RuntimeException("Не заданы серии данных для графика!");
        outputA = line1;
        outputB = line2;
        launch();
    }

    @Override
    public void start(Stage stage) {
        Stage stage1 = new Stage();
        stage1.setTitle("График");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Номер итерации");
        final LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        series.setName("ДМЧ - 1");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("ДМЧ - 2");
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        xAxis.setTickUnit(1);
        yAxis.setTickUnit(1);
        Scene scene = new Scene(lineChart, 800, 600);
        scene.getStylesheets().add(PATH_TO_CSS);
        for (int i = 0; i < outputA.size(); i++) {
            series.getResponseData().add(new XYChart.ResponseData(i, outputA.get(i)));
            series2.getResponseData().add(new XYChart.ResponseData(i, outputB.get(i)));
        }
        lineChart.getResponseData().addAll(series, series2);
        stage1.setScene(scene);
        stage1.show();
    }
}*/