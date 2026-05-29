package org.example.Visualizer;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.PointLight;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.scene.AmbientLight;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.PhongMaterial;

public class LatticeView1 {
    SubScene subScene;
    PerspectiveCamera camera;
    private Sphere[] atoms;
    private double[] positionX;
    private double[] positionY;
    private double[] positionZ;
    private Group root = new Group();
    private Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);

    private final int widthScene = 600;
    private final int hightScene = 430;
    private double mouseOldX, mouseOldY;

    // Material Atomics
    PhongMaterial material;
    PhongMaterial bondMaterial;

    // Size Lattice
    private int N;

    // Bonds
    private java.util.List<Bond> bonds = new java.util.ArrayList<>();

    public LatticeView1(
            int nx,
            int ny,
            int nz,
            double[] positionsX,
            double[] positionsY,
            double[] positionsZ) {
        this.N = nx * ny * nz;

        // Init arrays
        this.positionX = positionsX;
        this.positionY = positionsY;
        this.positionZ = positionsZ;

        // Rotate
        root.getTransforms().addAll(xRotate, yRotate);

        // Create Model
        createModel();
    }

    private void createScene() {
        /*
         * +--------------------+
         * |Create Scene: |
         * |1. Geometry scene. |
         * |2. Background scene.|
         * |3. Mouse pressed. |
         * |4. Mouse dragged. |
         * |5. Scrolling. |
         * +--------------------+
         */
        subScene = new SubScene(
                root,
                widthScene,
                hightScene,
                true,
                SceneAntialiasing.BALANCED);
        subScene.setFill(
                Color.rgb(
                        10,
                        10,
                        20));

        subScene.setOnMousePressed(e -> {
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });
        subScene.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - mouseOldX;
            double dy = e.getSceneY() - mouseOldY;

            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
            yRotate.setAngle(yRotate.getAngle() + dx * 0.5);
            xRotate.setAngle(xRotate.getAngle() - dy * 0.5);
        });
        subScene.setOnScroll(e -> {
            double zoom = e.getDeltaY();
            camera.setTranslateZ(camera.getTranslateZ() + zoom * 0.03);
        });
    }

    private void lightScene() {
        /*
         * +-----------------------+
         * |Light: ambient, point. |
         * +----------------------+
         */
        AmbientLight ambient = new AmbientLight(Color.WHITE);
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(20);
        light.setTranslateY(-20);
        light.setTranslateZ(-50);
        root.getChildren().addAll(ambient, light);
    }

    private void createCamera() {
        /*
         * +--------------+
         * |Create camera.|
         * +--------------+
         */
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-35);
        camera.setTranslateY(6);
        camera.setFarClip(200);
        subScene.setCamera(camera);
    }

    private void materialsAtomics() {
        /*
         * +------------------+
         * |Materials Atomics.|
         * +------------------+
         */
        material = new PhongMaterial();
        material.setDiffuseColor(Color.BLUEVIOLET);
        material.setSpecularColor(Color.WHITE);
        material.setSpecularPower(32);

        bondMaterial = new PhongMaterial();
        // Робимо їх напівпрозорими (останній параметр 0.4 - це прозорість)
        bondMaterial.setDiffuseColor(Color.rgb(150, 150, 150, 0.4));
        bondMaterial.setSpecularColor(Color.WHITE);
        bondMaterial.setSpecularPower(16);
    }

    private void createSpheres() {
        /*
         * +---------------+
         * |Create Spheres.|
         * +---------------+
         */
        atoms = new Sphere[N];

        for (int i = 0; i < N; i++) {
            Sphere s = new Sphere(0.18);
            s.setMaterial(material);
            s.setTranslateX(positionX[i]);
            s.setTranslateY(positionY[i]);
            s.setTranslateZ(positionZ[i]);
            atoms[i] = s;
            root.getChildren().add(s);
        }
    }

    private void createBonds() {
        double rCut = 1.6;
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                double dx = positionX[j] - positionX[i];
                double dy = positionY[j] - positionY[i];
                double dz = positionZ[j] - positionZ[i];
                double r_2 = dx * dx + dy * dy + dz * dz;

                if (r_2 < rCut * rCut) {
                    Point3D p1 = new Point3D(positionX[i], positionY[i], positionZ[i]);
                    Point3D p2 = new Point3D(positionX[j], positionY[j], positionZ[j]);

                    Cylinder cyl = createConnection(p1, p2);
                    cyl.setMaterial(bondMaterial);
                    bonds.add(new Bond(i, j, cyl));
                    root.getChildren().add(cyl);
                }
            }
        }
    }

    private void createModel() {
        /*
         * +--------------------------+
         * |Create Lattice 3D visual: |
         * |1. Create Lattice.        |
         * |2. Create spheres.        |
         * |3. Create bonds.          |
         * |4. Light scene.           |
         * |5. Create scene.          |
         * |6. Create camera.         |
         * +--------------------------+
         */
        materialsAtomics();
        createSpheres();
        createBonds();
        lightScene();
        createScene();
        createCamera();
    }

    public void setPositionsX(double[] positionsX) {
        if (positionsX == null)
            return;
        this.positionX = positionsX;
    }

    public void setPositionsY(double[] positionsY) {
        if (positionsY == null)
            return;
        this.positionY = positionsY;
    }

    public void setPositionsZ(double[] positionsZ) {
        if (positionsZ == null)
            return;
        this.positionZ = positionsZ;
    }

    public void updatePositions() {
        for (int i = 0; i < N; i++) {
            atoms[i].setTranslateX(positionX[i]);
            atoms[i].setTranslateY(positionY[i]);
            atoms[i].setTranslateZ(positionZ[i]);
        }

        Point3D yAxis = new Point3D(0, 1, 0);
        for (Bond bond : bonds) {
            int i = bond.i;
            int j = bond.j;

            double dx = positionX[j] - positionX[i];
            double dy = positionY[j] - positionY[i];
            double dz = positionZ[j] - positionZ[i];

            if (Math.abs(dx) > 3.0 || Math.abs(dy) > 3.0 || Math.abs(dz) > 3.0) {
                bond.cyl.setVisible(false);
                continue;
            } else {
                bond.cyl.setVisible(true);
            }

            Point3D p1 = new Point3D(positionX[i], positionY[i], positionZ[i]);
            Point3D p2 = new Point3D(positionX[j], positionY[j], positionZ[j]);
            Point3D diff = p2.subtract(p1);
            double height = diff.magnitude();

            bond.cyl.setHeight(height);
            Point3D mid = p2.midpoint(p1);
            bond.cyl.setTranslateX(mid.getX());
            bond.cyl.setTranslateY(mid.getY());
            bond.cyl.setTranslateZ(mid.getZ());

            Point3D axisOfRotation = diff.crossProduct(yAxis);
            double angle = Math.acos(diff.normalize().dotProduct(yAxis));

            bond.cyl.getTransforms().clear();
            if (axisOfRotation.magnitude() > 1e-6) {
                Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
                bond.cyl.getTransforms().add(rotateAroundCenter);
            }
        }
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public SubScene getSubScene() {
        return subScene;
    }

    public Cylinder createConnection(Point3D p1, Point3D p2) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = p2.subtract(p1);
        double height = diff.magnitude();

        // Зробимо їх значно тоншими (радіус 0.01 замість 0.05)
        Cylinder cylinder = new Cylinder(0.01, height);

        Point3D mid = p2.midpoint(p1);
        cylinder.setTranslateX(mid.getX());
        cylinder.setTranslateY(mid.getY());
        cylinder.setTranslateZ(mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        cylinder.getTransforms().add(rotateAroundCenter);
        return cylinder;
    }

    private class Bond {
        int i, j;
        Cylinder cyl;

        Bond(int i, int j, Cylinder cyl) {
            this.i = i;
            this.j = j;
            this.cyl = cyl;
        }
    }
}
