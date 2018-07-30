package application.view;

import javafx.scene.layout.Pane;

public class FullPane {
	private IController controller;
    private String style;
    private Pane pane;

    public FullPane(Pane pane, IController controller, String style) {
    	this.pane = pane;
    	this.controller = controller;
    	this.style = style;
    }

    public FullPane(IController controller, String style) {
    	this.controller = controller;
    	this.style = style;
    }

    public IController getController() {
    	return controller;
    }

    public String getStyle() {
    	return style;
    }

    public Pane getPane() {
    	return pane;
    }

    public void setController(IController controller) {
		this.controller = controller;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setPane(Pane pane) {
		this.pane = pane;
	}


}
