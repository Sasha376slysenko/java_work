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
    private Sphere[] atoms;
    private double[] positionX;
    private double[] positionY;
    private double[] positionZ;
    private Cylinder[] cylinders;

    SubScene subScene;
    PerspectiveCamera camera;
    private Group root = new Group();
    private Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);

    private int N;
    private double Lx;
    private double Ly;
    private double Lz;
    private double mouseOldX, mouseOldY;

    // Material Atomics
    private PhongMaterial material;
    private PhongMaterial bondMaterial;

    public LatticeView1(
            int nx,
            int ny,
            int nz,
            double latticeConstant,
            double[] positionsX,
            double[] positionsY,
            double[] positionsZ) {
        // Lattice parameters
        this.Lx = nx * latticeConstant;
        this.Ly = ny * latticeConstant;
        this.Lz = nz * latticeConstant;
        this.N = positionsX.length;

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
         * |Create Scene:       |
         * |1. Geometry scene.  |
         * |2. Background scene.|
         * |3. Mouse pressed.   |
         * |4. Mouse dragged.   |
         * |5. Scrolling.       |
         * +--------------------+
         */
        final int widthScene = 800;
        final int hightScene = 430;

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
        camera.setTranslateZ(-15);
        camera.setTranslateY(4);
        camera.setTranslateX(3);
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
        material.setDiffuseColor(Color.DARKGREEN);
        material.setSpecularColor(Color.WHITE);
        material.setSpecularPower(32);

        bondMaterial = new PhongMaterial();
        bondMaterial.setDiffuseColor(Color.WHITE);
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
        /*
        * +-----------------------+
        * |Create bonds: Cylynder.|
        * +-----------------------+
        */
        int nBonds = N * 8;
        cylinders = new Cylinder[nBonds];

        for (int i = 0; i < nBonds; i++) {
            Cylinder cyl = new Cylinder();
            cyl.setMaterial(bondMaterial);
            cyl.setVisible(false);
            cylinders[i] = cyl;
            root.getChildren().add(cyl);
        }
    }

    private void connectCylinder(Point3D p1, Point3D p2, Cylinder cyl) {
        /*
        * +-------------------------------------+
        * |Cylinder => (X, Y, Z), rotate, hight.|
        * +-------------------------------------+
        */
        Point3D diff = p2.subtract(p1);
        double height = diff.magnitude();
        cyl.setHeight(height);

        Point3D mid = p2.midpoint(p1);
        cyl.setTranslateX(mid.getX());
        cyl.setTranslateY(mid.getY());
        cyl.setTranslateZ(mid.getZ());

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        cyl.getTransforms().clear();
        cyl.getTransforms().add(rotateCenter);
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

    public void updatePositions() {
        /*
        * +------------------------------------------+
        * |Update position sphers and bonds:         |
        * |1. Update positions Spheres.              |
        * |2. Update posiotns culynders.             |
        * |3. Update rotate cylinders.               |
        * |4. Update cylinders: current connections. |
        * +------------------------------------------+
        */
        int usedCount = 0;
        double radius = 0.012;
        double maxBondLength = 3.8;

        // 1. Update positions Spheres.
        for (int i = 0; i < N; i++) {
            atoms[i].setTranslateX(positionX[i]);
            atoms[i].setTranslateY(positionY[i]);
            atoms[i].setTranslateZ(positionZ[i]);
        }

        // 2. Update posiotns culynders.
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                double dx = positionX[j] - positionX[i];
                double dy = positionY[j] - positionY[i];
                double dz = positionZ[j] - positionZ[i];

                if (Math.abs(dx) > Lx / 2 ||
                    Math.abs(dy) > Ly / 2 ||
                    Math.abs(dz) > Lz / 2) {
                        continue;
                }
                double r_2 = dx * dx + dy * dy + dz * dz;

                if (r_2 > maxBondLength * maxBondLength)
                    continue;
                if (usedCount > cylinders.length - 1)
                    continue;

                Cylinder cyl = cylinders[usedCount];
                cyl.setVisible(true);
                cyl.setRadius(radius);
                
                // 3. Update rotate cylinders.
                Point3D p1 = new Point3D(positionX[i], positionY[i], positionZ[i]);
                Point3D p2 = new Point3D(positionX[j], positionY[j], positionZ[j]);
                connectCylinder(p1, p2, cyl);
                usedCount++;
            }
        }

        // 4. Update cylinders: current connections.
        for (int k = usedCount; k < cylinders.length; k++) {
            cylinders[k].setVisible(false);
        }
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

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public SubScene getSubScene() {
        return subScene;
    }
}
