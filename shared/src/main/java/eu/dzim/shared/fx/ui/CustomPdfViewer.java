package eu.dzim.shared.fx.ui;

import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import eu.dzim.shared.disposable.Disposable;
import eu.dzim.shared.fx.fxml.FXMLLoaderService;
import eu.dzim.shared.fx.ui.controller.ImageContainerController;
import eu.dzim.shared.fx.util.PlatformHelper;
import eu.dzim.shared.fx.util.UIComponentType;
import eu.dzim.shared.resource.BaseResource;
import eu.dzim.shared.resource.Resource;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;

public class CustomPdfViewer extends BorderPane implements Disposable {
	
	private static final Logger LOG = LogManager.getLogger(CustomPdfViewer.class);
	
	private static final double ZOOM_MIN = 0.2;
	private static final double ZOOM_MAX = 2.0;
	private static final double ZOOM_DELTA = 1.05;
	
	private FXMLLoaderService fxmlLoaderService;
	private BaseResource baseResource;
	private Resource resource;
	
	private ValidationSupport validationSupport;
	
	private Path projectPath;
	
	@FXML private HBox fileImportBox;
	@FXML private Button fileImportButton;
	
	@FXML private Button fileSaveButton;
	@FXML private Button fileSaveAndOpenButton;
	
	@FXML private Button zoomFitPageButton;
	@FXML private Button zoomFitWidthButton;
	@FXML private Button zoomMinusButton;
	@FXML private Label zoomAmountLabel;
	@FXML private Button zoomPlusButton;
	
	@FXML private StackPane contentStackPane;
	
	@FXML private HBox contentSwipeParent;
	@FXML private SwipePane contentSwipePane;
	
	@FXML private ProgressIndicator contentLoadingProgress;
	
	@FXML private Button firstPageButton;
	@FXML private Button pageBackButton;
	@FXML private TextField pageSelectionText;
	@FXML private Label pageSelectionSeparatorLabel;
	@FXML private Label pageNumberLabel;
	@FXML private Button pageForwardButton;
	@FXML private Button lastPageButton;
	
	private final BooleanProperty allowFileLoading = new SimpleBooleanProperty(this, "allowFileLoading", false);
	private final ObjectProperty<Path> pdfFilePath = new SimpleObjectProperty<>(this, "pdfFilePath", null);
	
	private final ObjectProperty<PDFFile> pdfFile = new SimpleObjectProperty<>(this, "pdfFile", null);
	private final ObservableList<Image> pdfFilePages = FXCollections.observableArrayList();
	
	private final BooleanProperty loading = new SimpleBooleanProperty(this, "loading", false);
	private final DoubleProperty zoom = new SimpleDoubleProperty(this, "zoom", 1.0);
	
	private FileChooser fileChooser;
	private ExecutorService imageLoadService;
	
	public CustomPdfViewer() {
		try {
			getLoader(SharedUIComponentType.CUSTOM_PDF_VIEWER).load();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new RuntimeException(e);
		}
		validationSupport = new ValidationSupport();
	}
	
	private FXMLLoader getLoader(UIComponentType component) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(component.getAbsoluteLocation()));
		loader.setRoot(this);
		loader.setController(this);
		return loader;
	}
	
	public FXMLLoaderService getFxmlLoaderService() {
		return fxmlLoaderService;
	}
	
	public void setFxmlLoaderService(FXMLLoaderService fxmlLoaderService) {
		this.fxmlLoaderService = fxmlLoaderService;
	}
	
	public BaseResource getBaseResource() {
		return baseResource;
	}
	
	public void setBaseResource(BaseResource baseResource) {
		this.baseResource = baseResource;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public Path getProjectPath() {
		return projectPath;
	}
	
	public void setProjectPath(Path projectPath) {
		this.projectPath = projectPath;
	}
	
	public final BooleanProperty allowFileLoadingProperty() {
		return this.allowFileLoading;
	}
	
	public final boolean isAllowFileLoading() {
		return this.allowFileLoadingProperty().get();
	}
	
	public final void setAllowFileLoading(final boolean allowFileLoading) {
		this.allowFileLoadingProperty().set(allowFileLoading);
	}
	
	public ObservableList<Image> getPdfFilePages() {
		return pdfFilePages;
	}
	
	public void clear() {
		contentSwipePane.setIndex(0);
		contentSwipePane.getPanes().clear();
		pdfFilePages.clear();
		pageSelectionText.setText("0");
	}
	
	@FXML
	private void initialize() {
		
		if (fxmlLoaderService == null) {
			LOG.warn("Using internal alternative to FXMLLoaderService!");
			fxmlLoaderService = new AlternativeFXMLLoaderService();
		}
		
		createAndConfigureImageLoadService();
		createAndConfigureFileChooser();
		
		fileImportBox.managedProperty().bind(fileImportBox.visibleProperty());
		fileImportBox.visibleProperty().bind(allowFileLoading);
		
		contentSwipeParent.managedProperty().bind(contentSwipePane.visibleProperty());
		contentSwipePane.managedProperty().bind(contentSwipePane.visibleProperty());
		contentLoadingProgress.managedProperty().bind(contentLoadingProgress.visibleProperty());
		contentLoadingProgress.visibleProperty().bind(loading);
		
		getTop().disableProperty().bind(loading);
		getBottom().disableProperty().bind(loading);
		
		pdfFilePath.addListener(this::handleFilePathChanges);
		
		loading.addListener(this::handleLoadingChanges);
		
		pdfFile.addListener(this::handlePDFFileChanges);
		
		zoomAmountLabel.textProperty().bind(zoom.multiply(100.0).asString(Locale.ROOT, "%.0f %%"));
		
		pageSelectionText.setText("0");
		pageNumberLabel.setText("0");
		
		pageNumberLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			return pdfFile.get() == null ? "0" : "" + pdfFile.get().getNumPages();
		}, pdfFile));
		
		contentSwipePane.indexProperty().addListener((obs, o, n) -> {
			pageSelectionText.setText("" + (n.intValue() + 1));
		});
		
		pageSelectionText.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
			if (KeyCode.ENTER == keyEvent.getCode()) {
				String s = pageSelectionText.getText();
				try {
					int i = Integer.parseInt(s);
					if (contentSwipePane.getPanes().size() > 0 && (i - 1) < contentSwipePane.getPanes().size()) {
						contentSwipePane.setIndex(i - 1);
						keyEvent.consume();
					}
				} catch (NumberFormatException e) {}
			}
		});
		
		// TODO translate, if resource is set
	}
	
	private void createAndConfigureImageLoadService() {
		imageLoadService = Executors.newSingleThreadExecutor(r -> {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		});
	}
	
	private void createAndConfigureFileChooser() {
		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf", "*.PDF"));
	}
	
	private void handleFilePathChanges(ObservableValue<? extends Path> obs, Path o, Path n) {
		if (n == null)
			return;
		PlatformHelper.run(() -> loadFile(n.toFile()));
	}
	
	private void loadFile(File file) {
		if (file != null) {
			final Task<Pair<PDFFile, List<Image>>> loadFileTask = new Task<Pair<PDFFile, List<Image>>>() {
				@Override
				protected Pair<PDFFile, List<Image>> call() throws Exception {
					
					try (RandomAccessFile raf = new RandomAccessFile(file, "r"); FileChannel channel = raf.getChannel()) {
						
						ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
						PDFFile pdfFile = new PDFFile(buffer);
						
						List<Image> pages = new ArrayList<>();
						for (int index = 1; index <= pdfFile.getNumPages(); index++) {
							
							PDFPage page = pdfFile.getPage(index, true);
							Rectangle2D bbox = page.getBBox();
							
							// create the image
							Rectangle rect = new Rectangle(0, 0, (int) bbox.getWidth(), (int) bbox.getHeight());
							BufferedImage buffImage = new BufferedImage((int) (bbox.getWidth() * 2.0), (int) (bbox.getHeight() * 2.0),
									BufferedImage.TYPE_INT_RGB);
							java.awt.Image awtImage = page.getImage((int) (bbox.getWidth() * 2.0), (int) (bbox.getHeight() * 2.0), rect, null, true,
									true);
							java.awt.Graphics2D bufImageGraphics = buffImage.createGraphics();
							bufImageGraphics.drawImage(awtImage, 0, 0, null);
							
							// convert to JavaFX image:
							Image image = SwingFXUtils.toFXImage(buffImage, null);
							
							pages.add(image);
						}
						return new Pair<>(pdfFile, pages);
					}
				}
			};
			loadFileTask.setOnSucceeded(e -> {
				final Pair<PDFFile, List<Image>> result = loadFileTask.getValue();
				pdfFilePages.setAll(result.getValue());
				pdfFile.set(result.getKey());
				loading.set(false);
			});
			loadFileTask.setOnFailed(e -> {
				// showErrorMessage("Could not load file " + file.getName(), loadFileTask.getException());
				LOG.warn("Could not load file " + file.getName(), e.getSource().getException());
				pdfFilePages.clear();
				pdfFile.set(null);
				loading.set(false);
			});
			
			loading.set(true);
			imageLoadService.submit(loadFileTask);
		}
	}
	
	private void handleLoadingChanges(ObservableValue<? extends Boolean> obs, Boolean o, Boolean n) {
		if (n == null)
			return;
		contentSwipeParent.visibleProperty().set(!n);
	}
	
	private void handlePDFFileChanges(ObservableValue<? extends PDFFile> obs, PDFFile o, PDFFile n) {
		
		if (n == null) {
			clear();
			pageSelectionText.setText("0");
			registerInteger(pageSelectionText, "pdf.viewer.pageValidation.range", 0, 0);
			return;
		}
		
		pageSelectionText.setText("1");
		registerInteger(pageSelectionText, "pdf.viewer.pageValidation.range", 1, n.getNumPages());
		
		for (Image img : pdfFilePages) {
			
			SwipePanePlaceholder<ImageContainerController.ImageData> imgPlaceholder = new SwipePanePlaceholder<>(fxmlLoaderService,
					SharedUIComponentType.IMAGE_CONTAINER, true);
			javafx.geometry.Rectangle2D fitDimensions = new javafx.geometry.Rectangle2D(0.0, 0.0, contentSwipePane.getWidth(),
					contentSwipePane.getHeight());
			ImageContainerController.ImageData data = new ImageContainerController.ImageData(img, fitDimensions, zoom);
			imgPlaceholder.setData(data);
			imgPlaceholder.setShow(true);
			contentSwipePane.getPanes().add(imgPlaceholder);
		}
		
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Thread.sleep(10);
				return null;
			}
		};
		task.setOnSucceeded(e -> {
			zoomFit(true, true);
			zoomFit(true, false);
			contentSwipePane.setIndex(0);
		});
		new Thread(task).start();
	}
	
	@FXML
	private void loadFile(ActionEvent event) {
		final File file = fileChooser.showOpenDialog(getScene().getWindow());
		pdfFilePath.set(file == null ? null : Paths.get(file.getAbsolutePath()));
	}
	
	@FXML
	private void saveFile(ActionEvent event) {
		if (pdfFilePath.get() == null)
			return;
		if (projectPath == null)
			return;
		Path target = projectPath.resolve(pdfFilePath.get().getFileName().toString());
		try {
			Files.copy(pdfFilePath.get(), target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@FXML
	private void openFile(ActionEvent event) {
		if (pdfFilePath.get() == null)
			return;
		SwingUtilities.invokeLater(() -> {
			try {
				Desktop.getDesktop().open(pdfFilePath.get().toFile());
			} catch (IOException e) {
				LOG.warn(e.getMessage(), e);
			}
		});
	}
	
	@FXML
	private void zoomOut(ActionEvent event) {
		if (zoom.get() < ZOOM_MIN)
			return;
		PlatformHelper.run(() -> zoom.set(zoom.get() / ZOOM_DELTA));
	}
	
	@FXML
	private void zoomIn(ActionEvent event) {
		if (zoom.get() > ZOOM_MAX)
			return;
		PlatformHelper.run(() -> zoom.set(zoom.get() * ZOOM_DELTA));
	}
	
	@FXML
	private void zoomFit() {
		zoomFit(true, true);
	}
	
	@FXML
	private void zoomWidth() {
		zoomFit(true, false);
	}
	
	private void zoomFit(boolean useWidth, boolean useHeight) {
		int index = contentSwipePane.getIndex();
		if (index < 0)
			return;
		Image img = pdfFilePages.get(index);
		Pane content = contentSwipePane.getPanes().get(index).getContent();
		ScrollPane scroll = (ScrollPane) content.lookup("#scroll");
		double scrollWidth = scroll.getWidth();
		double scrollHeight = scroll.getHeight();
		double imgWidth = img.getWidth();
		double imgHeight = img.getHeight();
		double scale = ZOOM_MAX;
		if (useWidth) {
			while (ZOOM_MIN < scale && imgWidth * scale > scrollWidth) {
				scale /= ZOOM_DELTA;
			}
			scale /= ZOOM_DELTA;
		}
		if (useHeight) {
			while (ZOOM_MIN < scale && imgHeight * scale > scrollHeight) {
				scale /= ZOOM_DELTA;
			}
		}
		final double fScale = scale;
		PlatformHelper.run(() -> zoom.set(fScale));
	}
	
	@FXML
	private void pageFirst(ActionEvent event) {
		if (contentSwipePane.getPanes().size() > 0)
			contentSwipePane.setIndex(0);
	}
	
	@FXML
	private void pageBack(ActionEvent event) {
		if (contentSwipePane.getPanes().size() > 0 && contentSwipePane.getIndex() > 0)
			contentSwipePane.setIndex(contentSwipePane.getIndex() - 1);
	}
	
	@FXML
	private void pageNext(ActionEvent event) {
		if (contentSwipePane.getPanes().size() > 0 && contentSwipePane.getIndex() < contentSwipePane.getPanes().size() - 1)
			contentSwipePane.setIndex(contentSwipePane.getIndex() + 1);
	}
	
	@FXML
	private void pageLast(ActionEvent event) {
		if (contentSwipePane.getPanes().size() > 0)
			contentSwipePane.setIndex(contentSwipePane.getPanes().size() - 1);
	}
	
	private static class AlternativeFXMLLoaderService implements FXMLLoaderService {
		
		@Override
		public FXMLLoader getLoader() {
			return new FXMLLoader();
		}
		
		@Override
		public FXMLLoader getLoader(URL location) {
			return new FXMLLoader(location);
		}
		
		@Override
		public FXMLLoader getLoader(UIComponentType component) {
			return getLoader(getClass().getResource(component.getAbsoluteLocation()));
		}
		
		@Override
		public FXMLLoader getLoader(URL location, ResourceBundle resourceBundle) {
			return new FXMLLoader(location, resourceBundle);
		}
		
		@Override
		public FXMLLoader getLoader(UIComponentType component, ResourceBundle resourceBundle) {
			return getLoader(getClass().getResource(component.getAbsoluteLocation()), resourceBundle);
		}
	}
	
	public final ObjectProperty<Path> pdfFilePathProperty() {
		return this.pdfFilePath;
	}
	
	public final Path getPdfFilePath() {
		return this.pdfFilePathProperty().get();
	}
	
	public final void setPdfFilePath(final Path pdfFilePath) {
		this.pdfFilePathProperty().set(pdfFilePath);
	}
	
	@Override
	public void dispose() {
		imageLoadService.shutdownNow();
	}
	
	public void registerInteger(TextField textField, String toolTipKey, Integer min, Integer max) {
		// Dissallow all non number inputs
		textField.addEventFilter(KeyEvent.KEY_TYPED, (t) -> {
			char ar[] = t.getCharacter().toCharArray();
			char ch = ar[t.getCharacter().toCharArray().length - 1];
			if (!(ch >= '0' && ch <= '9')) {
				t.consume();
			}
		});
		int rMin = (min != null) ? min.intValue() : Integer.MIN_VALUE;
		int rMax = (max != null) ? max.intValue() : Integer.MAX_VALUE;
		validationSupport.registerValidator(textField, false, createInteger(rMin, rMax, toolTipKey));
	}
	
	private Validator<String> createInteger(final Integer min, final Integer max, final String toolTipKey) {
		return (Control c, String value) -> {
			Optional<String> rangeIndicator = Optional.empty();
			if (baseResource != null) {
				if (min != null && max != null) {
					rangeIndicator = baseResource.getString("generic.range.tri", min, max);
				} else if (min != null && max == null) {
					rangeIndicator = baseResource.getString("generic.range.duoMin", min);
				} else if (min == null && max != null) {
					rangeIndicator = baseResource.getString("generic.range.duoMax", max);
				}
			} else if (resource != null) {
				if (min != null && max != null) {
					rangeIndicator = resource.getString("generic.range.tri", min, max);
				} else if (min != null && max == null) {
					rangeIndicator = resource.getString("generic.range.duoMin", min);
				} else if (min == null && max != null) {
					rangeIndicator = resource.getString("generic.range.duoMax", max);
				}
			}
			String errorMessage = rangeIndicator.isPresent() ? " [" + rangeIndicator.get() + "]" : "";
			// Set Tooltip
			if (toolTipKey != null) {
				Optional<String> toolTipOpt = baseResource != null ? baseResource.getString(toolTipKey)
						: resource != null ? resource.getString(toolTipKey) : Optional.empty();
				errorMessage = (toolTipOpt.isPresent() ? toolTipOpt.get() : toolTipKey) + errorMessage;
				c.setTooltip(new Tooltip(errorMessage));
			}
			boolean allOK = true;
			try {
				int i = Integer.parseInt(value);
				if (min > i || i > max) {
					allOK = false;
				}
			} catch (NumberFormatException njfe) {
				allOK = false;
			}
			return ValidationResult.fromErrorIf(c, errorMessage, !allOK);
		};
	}
}
