package real_time_traffic_simulation_with_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

/**
 * MapPanel - Panel hiển thị bản đồ ở giữa với zoom controls
 * Sử dụng Affine Transform cho pan/zoom mượt mà và chính xác
 */
public class MapPanel extends StackPane {
    
    // Zoom settings
    private static final double MIN_SCALE = 0.5;
    private static final double MAX_SCALE = 3.0;
    private static final double ZOOM_STEP = 1.10;
    
    private double scale = 1.0;
    private double anchorX, anchorY;
    private double anchorTx, anchorTy;
    
    // Components
    private Pane viewport;
    private Group world;
    private ImageView imageView;
    private Affine viewTransform;
    
    /**
     * Constructor - Khởi tạo MapPanel với Affine Transform cho pan/zoom tối ưu
     */
    public MapPanel() {
        // Thiết lập style cho MapPanel
        setStyle("-fx-background-color: white; " +
                 "-fx-border-color: #bdbdbd; " +
                 "-fx-border-width: 0 2 0 2;");
        
        // Tạo viewport (Pane chứa world) - sẽ clip content
        viewport = new Pane();
        viewport.setStyle("-fx-background-color: white;");
        
        // Tạo world (Group chứa ImageView) - sẽ được transform
        world = new Group();
        
        // Load image 
        String imageUrl = getClass().getResource("resources/pngtree-pink-watercolor-brushes-png-image_5054156.jpg").toExternalForm();
        Image image = new Image(imageUrl);
        imageView = new ImageView(image);
        
        // Set kích thước ImageView để preserve aspect ratio
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        
        // Thêm ImageView vào world
        world.getChildren().add(imageView);
        
        // Tạo Affine transform cho world
        viewTransform = new Affine();
        world.getTransforms().setAll(viewTransform);
        
        // Thêm world vào viewport
        viewport.getChildren().add(world);
        
        // Bind clipping cho viewport để không tràn ra ngoài
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        
        // Bind kích thước ImageView theo viewport (fit to container)
        viewport.widthProperty().addListener((obs, old, newVal) -> centerImage());
        viewport.heightProperty().addListener((obs, old, newVal) -> centerImage());
        
        // Thêm viewport vào MapPanel
        getChildren().add(viewport);
        
        // Setup pan/zoom handlers
        setupPanZoom();
        
        // Tạo nút zoom + - macOS style
        Button zoomInBtn = new Button("+");
        zoomInBtn.setMinSize(36, 36);
        zoomInBtn.setMaxSize(36, 36);
        zoomInBtn.setPrefSize(36, 36);
        zoomInBtn.setStyle("-fx-background-color: #FFFFFF; " +
                          "-fx-border-color: #D1D1D6; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 6; " +
                          "-fx-background-radius: 6; " +
                          "-fx-font-size: 18px; " +
                          "-fx-font-weight: 600; " +
                          "-fx-text-fill: #007AFF; " +
                          "-fx-cursor: hand; " +
                          "-fx-padding: 0; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
        zoomInBtn.setOnMouseEntered(e -> 
            zoomInBtn.setStyle("-fx-background-color: #F5F5F7; " +
                          "-fx-border-color: #007AFF; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 6; " +
                          "-fx-background-radius: 6; " +
                          "-fx-font-size: 18px; " +
                          "-fx-font-weight: 600; " +
                          "-fx-text-fill: #007AFF; " +
                          "-fx-cursor: hand; " +
                          "-fx-padding: 0; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,122,255,0.3), 6, 0, 0, 2);")
        );
        zoomInBtn.setOnMouseExited(e -> 
            zoomInBtn.setStyle("-fx-background-color: #FFFFFF; " +
                          "-fx-border-color: #D1D1D6; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 6; " +
                          "-fx-background-radius: 6; " +
                          "-fx-font-size: 18px; " +
                          "-fx-font-weight: 600; " +
                          "-fx-text-fill: #007AFF; " +
                          "-fx-cursor: hand; " +
                          "-fx-padding: 0; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);")
        );
        zoomInBtn.setOnAction(e -> zoomIn());
        
        // Tạo nút zoom - - macOS style
        Button zoomOutBtn = new Button("−");
        zoomOutBtn.setMinSize(36, 36);
        zoomOutBtn.setMaxSize(36, 36);
        zoomOutBtn.setPrefSize(36, 36);
        zoomOutBtn.setStyle("-fx-background-color: #FFFFFF; " +
                           "-fx-border-color: #D1D1D6; " +
                           "-fx-border-width: 1; " +
                           "-fx-border-radius: 6; " +
                           "-fx-background-radius: 6; " +
                           "-fx-font-size: 20px; " +
                           "-fx-font-weight: 600; " +
                           "-fx-text-fill: #007AFF; " +
                           "-fx-cursor: hand; " +
                           "-fx-padding: 0; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
        zoomOutBtn.setOnMouseEntered(e -> 
            zoomOutBtn.setStyle("-fx-background-color: #F5F5F7; " +
                           "-fx-border-color: #007AFF; " +
                           "-fx-border-width: 1; " +
                           "-fx-border-radius: 6; " +
                           "-fx-background-radius: 6; " +
                           "-fx-font-size: 20px; " +
                           "-fx-font-weight: 600; " +
                           "-fx-text-fill: #007AFF; " +
                           "-fx-cursor: hand; " +
                           "-fx-padding: 0; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,122,255,0.3), 6, 0, 0, 2);")
        );
        zoomOutBtn.setOnMouseExited(e -> 
            zoomOutBtn.setStyle("-fx-background-color: #FFFFFF; " +
                           "-fx-border-color: #D1D1D6; " +
                           "-fx-border-width: 1; " +
                           "-fx-border-radius: 6; " +
                           "-fx-background-radius: 6; " +
                           "-fx-font-size: 20px; " +
                           "-fx-font-weight: 600; " +
                           "-fx-text-fill: #007AFF; " +
                           "-fx-cursor: hand; " +
                           "-fx-padding: 0; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);")
        );
        zoomOutBtn.setOnAction(e -> zoomOut());
        
        // Tạo VBox chứa 2 nút zoom
        VBox zoomControls = new VBox(2); // Khoảng cách 2px giữa các nút
        zoomControls.getChildren().addAll(zoomInBtn, zoomOutBtn);
        zoomControls.setStyle("-fx-background-color: transparent;");
        zoomControls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE); // Giữ kích thước nhỏ gọn
        
        // Thêm zoom controls vào panel
        getChildren().add(zoomControls);
        
        // Đặt zoom controls ở góc dưới bên phải - Responsive position
        StackPane.setAlignment(zoomControls, Pos.BOTTOM_RIGHT); // Chỉ set cho zoomControls thôi
        
        // Responsive margin - adjust based on viewport size
        widthProperty().addListener((obs, old, newVal) -> {
            double margin = newVal.doubleValue() > 800 ? 16 : 10;
            StackPane.setMargin(zoomControls, new Insets(0, margin, margin, 0));
        });
    }
    
    /**
     * Setup pan and zoom handlers với Affine Transform
     */
    private void setupPanZoom() {
        // Pan với chuột
        viewport.setOnMousePressed(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            anchorX = e.getX();
            anchorY = e.getY();
            anchorTx = viewTransform.getTx();
            anchorTy = viewTransform.getTy();
            viewport.setCursor(Cursor.CLOSED_HAND);
        });

        viewport.setOnMouseDragged(e -> {
            if (!e.isPrimaryButtonDown()) return;
            double dx = e.getX() - anchorX;
            double dy = e.getY() - anchorY;
            viewTransform.setTx(anchorTx + dx);
            viewTransform.setTy(anchorTy + dy);
        });

        viewport.setOnMouseReleased(e -> viewport.setCursor(Cursor.DEFAULT));

        // Zoom tại vị trí chuột với mouse wheel/touchpad
        viewport.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0) return;

            double factor = (e.getDeltaY() > 0) ? ZOOM_STEP : 1.0 / ZOOM_STEP;
            double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
            factor = newScale / scale;

            // Pivot trong tọa độ LOCAL của world
            Point2D pivot = world.sceneToLocal(e.getSceneX(), e.getSceneY());

            // Affine.appendScale với pivot local sẽ giữ pivot đứng yên trên màn hình
            viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());

            scale = newScale;
            e.consume();
        });
    }
    
    /**
     * Center image khi viewport thay đổi kích thước
     */
    private void centerImage() {
        if (viewport.getWidth() <= 0 || viewport.getHeight() <= 0) return;
        
        double viewportWidth = viewport.getWidth();
        double viewportHeight = viewport.getHeight();
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();
        
        // Tính scale để fit image vào viewport (contain)
        double scaleX = viewportWidth / imageWidth;
        double scaleY = viewportHeight / imageHeight;
        double fitScale = Math.min(scaleX, scaleY);
        
        // Set kích thước ImageView
        imageView.setFitWidth(imageWidth * fitScale);
        imageView.setFitHeight(imageHeight * fitScale);
        
        // Center ImageView trong world
        double offsetX = (viewportWidth - imageView.getFitWidth()) / 2;
        double offsetY = (viewportHeight - imageView.getFitHeight()) / 2;
        imageView.setX(offsetX);
        imageView.setY(offsetY);
    }
    
    /**
     * Clamp giá trị trong khoảng min-max
     */
    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
    
    /**
     * Phóng to hình ảnh (zoom in) - zoom tại center của viewport
     */
    private void zoomIn() {
        double factor = ZOOM_STEP;
        double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
        factor = newScale / scale;
        
        // Zoom tại center của viewport
        double centerX = viewport.getWidth() / 2;
        double centerY = viewport.getHeight() / 2;
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
    
    /**
     * Thu nhỏ hình ảnh (zoom out) - zoom tại center của viewport
     */
    private void zoomOut() {
        double factor = 1.0 / ZOOM_STEP;
        double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
        factor = newScale / scale;
        
        // Zoom tại center của viewport
        double centerX = viewport.getWidth() / 2;
        double centerY = viewport.getHeight() / 2;
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
}
