package application.util;

import application.style.Style;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LineChartDialog {

	private static void show(String title, String tagTitle, String valueTitle, String[] tags, double[] values,
			boolean regression) {

		int length = tags.length;

		if ((length != values.length) || (length == 0))
			return;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setWidth(600);
		window.setHeight(400);
		window.setResizable(true);

		Button okButton = new Button("Ok");

		okButton.setOnAction(e -> window.close());

		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		LineChart<String, Number> lc = new LineChart<String, Number>(xAxis, yAxis);
		lc.setTitle(title);
		xAxis.setLabel(tagTitle);
		yAxis.setLabel(valueTitle);

		Series<String, Number> serie = new XYChart.Series<String, Number>();
		for (int i = 0; i < values.length; i++)
			serie.getData().add(new Data<String, Number>(tags[i], values[i]));

		serie.setName(valueTitle);
		// lc.setLegendVisible(false);
		lc.getData().add(serie);

		VBox layout = new VBox(15);
		layout.setAlignment(Pos.CENTER);
		okButton.setAlignment(Pos.CENTER_RIGHT);

		layout.getChildren().add(lc);

		if (regression) {
			double[] rl = regressionLine(values);
			Series<String, Number> rlserie = new XYChart.Series<String, Number>();

			double p1 = rl[1];
			double p2 = rl[0] * (length - 1) + rl[1];

			rlserie.getData().add(new XYChart.Data<String, Number>(tags[0], p1));
			rlserie.getData().add(new XYChart.Data<String, Number>(tags[length - 1], p2));

			rlserie.setName("Progression");

			Label label = new Label();
			if (rl[0] > 0)
				label.setText("State: positive progression.");
			else if (rl[0] < 0)
				label.setText("State: negative progression.");
			else
				label.setText("State: no progression.");

			lc.getData().add(rlserie);
			layout.getChildren().add(label);
		}
		layout.getChildren().add(okButton);

		// layout.setId("pane");
		Scene scene = new Scene(layout);
		scene.getStylesheets().add(Style.getInstance().getResource(Style.STAT));
		window.setScene(scene);
		window.showAndWait();
	}

	public static void show(String title, String tagTitle, String valueTitle, String[] tags, double[] values) {
		show(title, tagTitle, valueTitle, tags, values, true);
	}

	public static void showNoRegression(String title, String tagTitle, String valueTitle, String[] tags,
			double[] values) {
		show(title, tagTitle, valueTitle, tags, values, false);
	}

	/*
	 * private static double[] regressionLine(double[] values) { int length =
	 * values.length;
	 *
	 * // Mean, covarianze and variance double mx = 0, my = 0, cov = 0, vx = 0;
	 * for (int i = 0; i < length; i++) { mx += i; my += values[i]; cov += i *
	 * values[i]; vx += i * i; } mx /= length; my /= length; cov = cov / length
	 * - mx * my; vx = vx / length - mx * mx;
	 *
	 * double m = cov / vx; double n = m * (-vx) + my;
	 *
	 * return new double[] { m, n };
	 *
	 * }
	 */

	// Least squares regression
	private static double[] regressionLine(double[] values) {
		int N = values.length;

		double xSumUp = 0, ySumUp = 0, x2SumUp = 0, xySumUp = 0;
		for (int i = 0; i < N; i++) {
			xSumUp += i;
			ySumUp += values[i];
			x2SumUp += i * i;
			xySumUp += i * values[i];
		}

		double m = (N * xySumUp - xSumUp * ySumUp) / (N * x2SumUp - xSumUp * xSumUp);
		double n = (ySumUp - m * xSumUp) / N;

		return new double[] { m, n };

	}
}
