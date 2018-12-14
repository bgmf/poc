package eu.dzim.shared.fx.ui.controller;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SimpleDialogController {

    private final ObjectProperty<AlertType> dialogType = new SimpleObjectProperty<>(AlertType.CONFIRMATION);
    @FXML private HBox headerContainer;
    @FXML private VBox iconContainer;
    @FXML private MaterialDesignIconView icon;
    @FXML private VBox titleContainer;
    @FXML private Label title;
    @FXML private Label subTitle;
    @FXML private VBox contentContainer;
    @FXML private Label messageText;

    @FXML
    protected void initialize() {

        dialogType.addListener((observable, oldValue, newValue) -> {
            MaterialDesignIcon iconType = MaterialDesignIcon.COMMENT_QUESTION_OUTLINE;
            if (newValue != null) {
                switch (newValue) {
                case CONFIRMATION:
                    iconType = MaterialDesignIcon.COMMENT_QUESTION_OUTLINE;
                    break;
                case ERROR:
                    iconType = MaterialDesignIcon.COMMENT_REMOVE_OUTLINE;
                    break;
                case INFORMATION:
                    iconType = MaterialDesignIcon.COMMENT_TEXT_OUTLINE;
                    break;
                case NONE:
                    iconType = MaterialDesignIcon.COMMENT_OUTLINE;
                    break;
                case WARNING:
                    iconType = MaterialDesignIcon.COMMENT_ALERT_OUTLINE;
                    break;
                }
            }
            icon.setGlyphName(iconType.name());
        });

        subTitle.visibleProperty().bind(subTitle.textProperty().isNotEmpty());
        subTitle.managedProperty().bind(subTitle.textProperty().isNotEmpty());
    }

    public final ObjectProperty<AlertType> dialogTypeProperty() {
        return this.dialogType;
    }

    public final AlertType getDialogType() {
        return this.dialogTypeProperty().get();
    }

    public final void setDialogType(final AlertType alertType) {
        this.dialogTypeProperty().set(alertType);
    }

    public final StringProperty titleProperty() {
        return this.title.textProperty();
    }

    public final String getTitle() {
        return this.titleProperty().get();
    }

    public final void setTitle(final String title) {
        this.titleProperty().set(title);
    }

    public final StringProperty subTitleProperty() {
        return this.subTitle.textProperty();
    }

    public final String getSubTitle() {
        return this.subTitleProperty().get();
    }

    public final void setSubTitle(final String subTitle) {
        this.subTitleProperty().set(subTitle);
    }

    public final StringProperty messageProperty() {
        return this.messageText.textProperty();
    }

    public final String getMessage() {
        return this.messageProperty().get();
    }

    public final void setMessage(final String message) {
        this.messageProperty().set(message);
    }
}
