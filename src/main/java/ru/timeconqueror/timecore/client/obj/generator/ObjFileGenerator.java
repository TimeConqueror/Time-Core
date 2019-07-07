package ru.timeconqueror.timecore.client.obj.generator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ObjFileGenerator {
    private File outputFile;
    private ModelBase model;

    private int currentCubeIndex = 1;

    private int textureWidth;
    private int textureHeight;

    private List<String> verticesGeometric = new ArrayList<>();
    private List<String> verticesTexture = new ArrayList<>();
    private List<String> verticesNormal = new ArrayList<>();

    private int lastIndexVerGeometric = 0;
    private int lastIndexVerTexture = 0;
    private int lastIndexVerNormal = 0;

    private float offsetX;
    private float offsetY;
    private float offsetZ;

    private float oldOffSetX;
    private float oldOffSetY;
    private float oldOffSetZ;

    private float rotatedX;
    private float rotatedY;
    private float rotatedZ;

    private float rotationAngleDegreeX;
    private float rotationAngleDegreeY;
    private float rotationAngleDegreeZ;

    private List<String> currentGroupFaces = new ArrayList<>();

    public ObjFileGenerator() {
    }

    public void create(String targetFolder, ModelBase modelIn, String entityName) {
        if (!createFile(targetFolder, entityName, "obj")) {
            return;
        }

        this.model = modelIn;

        textureWidth = model.textureWidth;
        textureHeight = model.textureHeight;

        try {
            processModelRendererList(model.boxList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("timecore.genobj.successgen"));
    }

    private void processModelRendererList(List<ModelRenderer> renderers) throws IOException {
        if (renderers == null) {
            return;
        }

        for (ModelRenderer renderer : renderers) {
            processModelRenderer(renderer);
        }
    }

    private void processModelRenderer(ModelRenderer renderer) throws IOException {
        if (!renderer.showModel || renderer.isHidden) {
            return;
        }

//        System.out.println("----START----");
//        System.out.println(offsetX + " " + offsetY + " " + offsetZ);

        offsetX += renderer.rotationPointX;
        offsetY += renderer.rotationPointY;
        offsetZ += renderer.rotationPointZ;

        rotationAngleDegreeX = renderer.rotateAngleX * (180F / (float) Math.PI);
        rotationAngleDegreeY = renderer.rotateAngleY * (180F / (float) Math.PI);
        rotationAngleDegreeZ = renderer.rotateAngleZ * (180F / (float) Math.PI);

        createNewGroup(renderer);

//        translateOffsetsByAngles(renderer);

        if (renderer.cubeList != null) {
            for (ModelBox modelBox : renderer.cubeList) {
                for (TexturedQuad texturedQuad : modelBox.quadList) {
                    processTexturedQuad(texturedQuad);
                }
            }
        }

        printGroupDataToFile();

        verticesGeometric.clear();
        verticesTexture.clear();
        verticesNormal.clear();
        currentGroupFaces.clear();

        processModelRendererList(renderer.childModels);

//        rotationAngleDegreeX -= renderer.rotateAngleX;
//        rotationAngleDegreeY -= renderer.rotateAngleY;
//        rotationAngleDegreeZ -= renderer.rotateAngleZ;
//        offsetX = oldOffSetX -= renderer.rotationPointX;
//        offsetY = oldOffSetY -= renderer.rotationPointY;
//        offsetZ = oldOffSetZ -= renderer.rotationPointZ;

        offsetX -= renderer.rotationPointX;
        offsetY -= renderer.rotationPointY;
        offsetZ -= renderer.rotationPointZ;

//        System.out.println(offsetX + " " + offsetY + " " + offsetZ);
//        System.out.println("---END---");
    }

    //FIXME rotations...
    private void translateOffsetsByAngles(ModelRenderer renderer) {
        oldOffSetX += renderer.rotationPointX;
        oldOffSetY += renderer.rotationPointY;
        oldOffSetZ += renderer.rotationPointZ;

        //rotate by x
        if (rotationAngleDegreeX != 0) {
            offsetY = (float) (offsetY * Math.cos(rotationAngleDegreeX) + offsetZ * Math.sin(rotationAngleDegreeX));
            offsetZ = (float) (offsetY * -Math.sin(rotationAngleDegreeX) + offsetZ * Math.cos(rotationAngleDegreeX));
        }

        //rotate by y
        if (rotationAngleDegreeY != 0) {
            offsetX = (float) (offsetX * Math.cos(rotationAngleDegreeY) + offsetZ * Math.sin(rotationAngleDegreeY));
            offsetZ = (float) (offsetX * -Math.sin(rotationAngleDegreeY) + offsetZ * Math.cos(rotationAngleDegreeY));
        }

        //rotate by z
        if (rotationAngleDegreeZ != 0) {
            offsetX = (float) (offsetX * Math.cos(rotationAngleDegreeZ) + offsetY * -Math.sin(rotationAngleDegreeZ));
            offsetY = (float) (offsetX * Math.sin(rotationAngleDegreeZ) + offsetY * Math.cos(rotationAngleDegreeZ));
        }
    }

    private void translateByAngles(float x, float y, float z) {
        rotatedX = x;
        rotatedY = y;
        rotatedZ = z;

        //rotate by x
//        if(rotationAngleDegreeX != 0) {
//            rotatedY = (float) (y * Math.cos(rotationAngleDegreeX) + z * Math.sin(rotationAngleDegreeX));
//            rotatedZ = (float) (y * -Math.sin(rotationAngleDegreeX) + z * Math.cos(rotationAngleDegreeX));
//        }

        //rotate by y
//        if(rotationAngleDegreeY != 0) {
//            rotatedX = (float) (x * Math.cos(rotationAngleDegreeY) + z * Math.sin(rotationAngleDegreeY));
//            rotatedZ = (float) (x * -Math.sin(rotationAngleDegreeY) + z * Math.cos(rotationAngleDegreeY));
//            System.out.println(rotationAngleDegreeY);
//        }

//        //rotate by z
//        if(rotationAngleDegreeZ != 0) {
//            rotatedX = (float) (x * Math.cos(rotationAngleDegreeZ) + y * -Math.sin(rotationAngleDegreeZ));
//            rotatedY = (float) (x * Math.sin(rotationAngleDegreeZ) + y * Math.cos(rotationAngleDegreeZ));
//        }
    }

    private void printGroupDataToFile() throws IOException {
        FileUtils.writeLines(outputFile, verticesGeometric, true);
        FileUtils.writeLines(outputFile, verticesTexture, true);
        FileUtils.writeLines(outputFile, verticesNormal, true);
        FileUtils.writeLines(outputFile, currentGroupFaces, true);
    }

    private void processTexturedQuad(TexturedQuad quad) {
        Vec3d vec3d = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[0].vector3D);
        Vec3d vec3d1 = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[2].vector3D);
        Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
        float vecNormalX = (float) vec3d2.x;
        float vecNormalY = (float) vec3d2.y;
        float vecNormalZ = (float) vec3d2.z;

        int firstIndexGeom = lastIndexVerGeometric + 1;
        int firstIndexTexture = lastIndexVerTexture + 1;
        int indexNormal = lastIndexVerNormal + 1;

        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex vertex = quad.vertexPositions[i];

            translateByAngles((float) vertex.vector3D.x, (float) vertex.vector3D.y, (float) vertex.vector3D.z);

//            String vertexGeom = "v " + (offsetX + (float) vertex.vector3D.x) + " " + (-1 * (offsetY + (float) vertex.vector3D.y)) + " " + (offsetZ + (float) vertex.vector3D.z);
            String vertexGeom = "v " + (offsetX + rotatedX) + " " + (-1 * (offsetY + rotatedY) + " " + (offsetZ + rotatedZ));
//            String vertexGeom = "v " + (rotatedX) + " " + (-1 * (rotatedY) + " " + (rotatedZ));
            verticesGeometric.add(vertexGeom);
            lastIndexVerGeometric++;

            String vertexTexture = "vt " + vertex.texturePositionX + " " + vertex.texturePositionY;
            verticesTexture.add(vertexTexture);
            lastIndexVerTexture++;
        }

        String vertexNormal = "vn " + vecNormalX + " " + vecNormalY + " " + vecNormalZ;
        verticesNormal.add(vertexNormal);
        lastIndexVerNormal++;

        String face = "f " +
                firstIndexGeom++ + "/" + firstIndexTexture++ + "/" + indexNormal + " " +
                firstIndexGeom++ + "/" + firstIndexTexture++ + "/" + indexNormal + " " +
                firstIndexGeom++ + "/" + firstIndexTexture++ + "/" + indexNormal + " " +
                firstIndexGeom + "/" + firstIndexTexture + "/" + indexNormal;
        currentGroupFaces.add(face);
    }

    private void createNewGroup(ModelRenderer renderer) throws IOException {
        String groupName = renderer.boxName == null || renderer.boxName.equals("null") ? "Cube." + currentCubeIndex++ : renderer.boxName;
        FileUtils.write(outputFile, "g " + groupName + "\n", Charsets.UTF_8, true);
    }

    private boolean createFile(String folder, String fileName, String extension) {
        File objFolder = new File(folder);

        if (!objFolder.exists() && !objFolder.mkdirs()) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("timecore.genobj.cantcreatefolder"));
            return false;
        }

        File file = new File(folder + "/" + fileName + "." + extension);
        boolean created;
        if (!file.exists()) {
            try {
                created = file.createNewFile();
            } catch (IOException e) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("timecore.genobj.cantcreatefile"));
                e.printStackTrace();
                return false;
            }
        } else {
            FileUtils.deleteQuietly(file);
            try {
                created = file.createNewFile();
            } catch (IOException e) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("timecore.genobj.cantcreatefile"));
                e.printStackTrace();
                return false;
            }
        }

        if (!created) {
            Minecraft.getMinecraft().player.sendChatMessage("Can't create output file. Obj file won't be created. Check logs for details.");
            return false;
        }

        outputFile = file;

        return true;
    }
}
