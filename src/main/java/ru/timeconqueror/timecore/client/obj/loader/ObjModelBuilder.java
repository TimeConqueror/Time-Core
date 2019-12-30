package ru.timeconqueror.timecore.client.obj.loader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelRenderer;
import ru.timeconqueror.timecore.client.obj.loader.part.Face;
import ru.timeconqueror.timecore.client.obj.loader.part.ModelObject;
import ru.timeconqueror.timecore.client.obj.loader.part.TextureCoordinate;
import ru.timeconqueror.timecore.client.obj.loader.part.Vertex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wavefront ModelObject importer
 * Based heavily off of the specifications found at http://en.wikipedia.org/wiki/Wavefront_.obj_file
 */
public class ObjModelBuilder {
    private static Pattern vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
    private static Pattern vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
    private static Pattern textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+(\\.\\d+)?){2,3} *$)");
    private static Pattern face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
    private static Pattern face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
    private static Pattern groupObjectPattern = Pattern.compile("([go]( [\\w\\d\\.]+) *\\n)|([go]( [\\w\\d\\.]+) *$)");

    private static Matcher vertexMatcher, vertexNormalMatcher, textureCoordinateMatcher;
    private static Matcher face_V_VT_VN_Matcher, face_V_VT_Matcher, face_V_VN_Matcher, face_V_Matcher;
    private static Matcher groupObjectMatcher;

    public ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Vertex> vertexNormals = new ArrayList<>();
    private ArrayList<TextureCoordinate> textureCoordinates = new ArrayList<>();
    private ModelObject currentModelObject;
    private ResourceLocation fileLocation;
    private ResourceLocation rpFileLocation;

    private ArrayList<ObjModelRenderer> renderers = new ArrayList<>();
    private ArrayList<RPVertex> rpVertexes = new ArrayList<>();

    public ObjModelBuilder(ResourceLocation rl) throws ModelFormatException {
        this.fileLocation = rl;
    }

    /***
     * Verifies that the given line from the model file is a valid vertex
     * @param line the line being validated
     * @return true if the line is a valid vertex, false otherwise
     */
    private static boolean isValidVertexLine(String line) {
        if (vertexMatcher != null) {
            vertexMatcher.reset();
        }

        vertexMatcher = vertexPattern.matcher(line);
        return vertexMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid vertex normal
     * @param line the line being validated
     * @return true if the line is a valid vertex normal, false otherwise
     */
    private static boolean isValidVertexNormalLine(String line) {
        if (vertexNormalMatcher != null) {
            vertexNormalMatcher.reset();
        }

        vertexNormalMatcher = vertexNormalPattern.matcher(line);
        return vertexNormalMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid texture coordinate
     * @param line the line being validated
     * @return true if the line is a valid texture coordinate, false otherwise
     */
    private static boolean isValidTextureCoordinateLine(String line) {
        if (textureCoordinateMatcher != null) {
            textureCoordinateMatcher.reset();
        }

        textureCoordinateMatcher = textureCoordinatePattern.matcher(line);
        return textureCoordinateMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices, texture coordinates, and vertex normals
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1/vt1/vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VT_VN_Line(String line) {
        if (face_V_VT_VN_Matcher != null) {
            face_V_VT_VN_Matcher.reset();
        }

        face_V_VT_VN_Matcher = face_V_VT_VN_Pattern.matcher(line);
        return face_V_VT_VN_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices and texture coordinates
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1/vt1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VT_Line(String line) {
        if (face_V_VT_Matcher != null) {
            face_V_VT_Matcher.reset();
        }

        face_V_VT_Matcher = face_V_VT_Pattern.matcher(line);
        return face_V_VT_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices and vertex normals
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1//vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VN_Line(String line) {
        if (face_V_VN_Matcher != null) {
            face_V_VN_Matcher.reset();
        }

        face_V_VN_Matcher = face_V_VN_Pattern.matcher(line);
        return face_V_VN_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by only vertices
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_Line(String line) {
        if (face_V_Matcher != null) {
            face_V_Matcher.reset();
        }

        face_V_Matcher = face_V_Pattern.matcher(line);
        return face_V_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face of any of the possible face formats
     * @param line the line being validated
     * @return true if the line is a valid face that matches any of the valid face formats, false otherwise
     */
    private static boolean isValidFaceLine(String line) {
        return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
    }

    /***
     * Verifies that the given line from the model file is a valid group (or object)
     * @param line the line being validated
     * @return true if the line is a valid group (or object), false otherwise
     */
    private static boolean isValidGroupObjectLine(String line) {
        if (groupObjectMatcher != null) {
            groupObjectMatcher.reset();
        }

        groupObjectMatcher = groupObjectPattern.matcher(line);
        return groupObjectMatcher.matches();
    }

    public ObjModel loadModel() throws ModelFormatException {

        int lineCount = 0;
        ObjModel model = new ObjModel();

        try (IResource objFile = Minecraft.getMinecraft().getResourceManager().getResource(fileLocation)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(objFile.getInputStream()))) {
                String currentLine;

                while ((currentLine = reader.readLine()) != null) {
                    lineCount++;
                    currentLine = currentLine.replaceAll("\\s+", " ").trim();

                    if (currentLine.startsWith("#") || currentLine.length() == 0) {
                        continue;
                    } else if (currentLine.startsWith("v ")) {
                        Vertex vertex = parseVertex(currentLine, lineCount);
                        if (vertex != null) {
                            vertices.add(vertex);
                        }
                    } else if (currentLine.startsWith("vn ")) {
                        Vertex vertex = parseVertexNormal(currentLine, lineCount);
                        if (vertex != null) {
                            vertexNormals.add(vertex);
                        }
                    } else if (currentLine.startsWith("vt ")) {
                        TextureCoordinate textureCoordinate = parseTextureCoordinate(currentLine, lineCount);
                        if (textureCoordinate != null) {
                            textureCoordinates.add(textureCoordinate);
                        }
                    } else if (currentLine.startsWith("f ")) {

                        if (currentModelObject == null) {
                            currentModelObject = new ModelObject("Default");
                        }

                        Face face = parseFace(currentLine, lineCount);

                        if (face != null) {
                            currentModelObject.faces.add(face);
                        }
                    } else if (currentLine.startsWith("g ") | currentLine.startsWith("o ")) {
                        ModelObject group = parseGroupObject(currentLine, lineCount);

                        if (group != null) {
                            if (currentModelObject != null) {
                                renderers.add(new ObjModelRenderer(model, currentModelObject));
                            }
                        }

                        currentModelObject = group;
                    }
                }

                renderers.add(new ObjModelRenderer(model, currentModelObject));
            }
        } catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }

        String[] path = fileLocation.getPath().split("/");
        String fileName = path[path.length - 1].split("\\.")[0];
        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < path.length - 1; i++) {
            if (i != 0) {
                newPath.append("/");
            }

            newPath.append(path[i]);
        }

        rpFileLocation = new ResourceLocation(fileLocation.getNamespace(), newPath + "/" + fileName + ".rp");

        loadRPFile();

        model.setParts(renderers);
        return model;
    }

    private void loadRPFile() {
        String type = null;
        int lineCount = 0;
        try (IResource objFile = Minecraft.getMinecraft().getResourceManager().getResource(rpFileLocation)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(objFile.getInputStream()))) {
                String currentLine;

                while ((currentLine = reader.readLine()) != null) {
                    lineCount++;
                    currentLine = currentLine.replaceAll("\\s+", " ").trim();

                    if (currentLine.isEmpty()) {
                        continue;
                    } else if (currentLine.startsWith("#")) {
                        continue;
                    } else if (currentLine.startsWith("!type") && lineCount == 1) {
                        type = currentLine.trim().split(" ")[1];
                    } else {
                        parseRPVertex(currentLine, lineCount);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            if (TimeCore.logHelper.isInDev()) {
                String[] path = rpFileLocation.getPath().split("/");
                TimeCore.logHelper.printDevOnlyMessage("No .rp file with the name " + path[path.length - 1] + " was found! All rotation points will be set to 0 by default.");
            }
            return;
        } catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }

        if (type != null && !type.isEmpty()) {
            if (type.equals("blender")) {
                for (RPVertex rpVertex : rpVertexes) {
                    float y = rpVertex.vertex.y;

                    rpVertex.vertex.y = rpVertex.vertex.z;
                    rpVertex.vertex.z = -y;
                }
            } else {
                TimeCore.logHelper.warn("Unknown type {} in .rp File with path {}", type, rpFileLocation);
            }
        }

        for (ObjModelRenderer renderer : renderers) {
            boolean found = false;
            for (RPVertex rpVertex : rpVertexes) {
                if (renderer.getName().equals(rpVertex.rendererName)) {
                    renderer.setRotationPoint(rpVertex.vertex);
                    found = true;
                    break;
                }
            }

            if (!found) {
                TimeCore.logHelper.printDevOnlyMessage("No rotation point vertex was found for renderer with name " + renderer.getName() + " in rp-file. They will be set to 0 by default.");
            }
        }
    }

    private void parseRPVertex(String line, int lineCount) throws ModelFormatException {

        String[] splittedByEqualSign = line.split("=");
        if (splittedByEqualSign.length != 2) {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + rpFileLocation + "' - \n" +
                    "The line must contain key and vertexData in such case: name_of_object=x/y/z");
        }

        String name = splittedByEqualSign[0];
        String vertexData = splittedByEqualSign[1];

        String[] vertexCoords = vertexData.split("/");
        if (vertexCoords.length != 3) {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + rpFileLocation + "' - Count of vertex coordinates must be equal 3.");
        }

        RPVertex vertex;
        try {
            vertex = new RPVertex(name, new Vertex(Float.parseFloat(vertexCoords[0]), Float.parseFloat(vertexCoords[1]), Float.parseFloat(vertexCoords[2])));
        } catch (NumberFormatException e) {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - \n" +
                    String.format("Number formatting error at line %d", lineCount), e);
        }

        rpVertexes.add(vertex);
    }

    private Vertex parseVertex(String line, int lineCount) throws ModelFormatException {
        if (isValidVertexLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {
                if (tokens.length == 2) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
                } else if (tokens.length == 3) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            } catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Incorrect format");
        }

        return null;
    }

    private Vertex parseVertexNormal(String line, int lineCount) throws ModelFormatException {
        if (isValidVertexNormalLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {
                if (tokens.length == 3)
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            } catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Incorrect format");
        }

        return null;
    }

    private TextureCoordinate parseTextureCoordinate(String line, int lineCount) throws ModelFormatException {
        if (isValidTextureCoordinateLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {
                if (tokens.length == 2)
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]));
                else if (tokens.length == 3)
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            } catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Incorrect format");
        }

        return null;
    }

    private Face parseFace(String line, int lineCount) throws ModelFormatException {
        Face face;

        if (isValidFaceLine(line)) {
            face = new Face();

            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            String[] tokens = trimmedLine.split(" ");
            String[] subTokens;

            if (tokens.length == 3) {
                if (currentModelObject.glDrawingMode == -1) {
                    currentModelObject.glDrawingMode = GL11.GL_TRIANGLES;
                } else if (currentModelObject.glDrawingMode != GL11.GL_TRIANGLES) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Invalid number of points for face (expected 4, found " + tokens.length + ")");
                }
            } else if (tokens.length == 4) {
                if (currentModelObject.glDrawingMode == -1) {
                    currentModelObject.glDrawingMode = GL11.GL_QUADS;
                } else if (currentModelObject.glDrawingMode != GL11.GL_QUADS) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Invalid number of points for face (expected 3, found " + tokens.length + ")");
                }
            }

            // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...
            if (isValidFace_V_VT_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1/vt1 v2/vt2 v3/vt3 ...
            else if (isValidFace_V_VT_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1//vn1 v2//vn2 v3//vn3 ...
            else if (isValidFace_V_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("//");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1 v2 v3 ...
            else if (isValidFace_V_Line(line)) {
                face.vertices = new Vertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    face.vertices[i] = vertices.get(Integer.parseInt(tokens[i]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            } else {
                throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Incorrect format");
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Incorrect format");

        }

        return face;
    }

    private ModelObject parseGroupObject(String line, int lineCount) throws ModelFormatException {
        ModelObject group = null;

        if (isValidGroupObjectLine(line)) {
            String trimmedLine = line.substring(line.indexOf(" ") + 1);

            if (trimmedLine.length() > 0) {
                group = new ModelObject(trimmedLine);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileLocation + "' - Incorrect format");
        }

        return group;
    }

    private class RPVertex {
        Vertex vertex;
        String rendererName;

        public RPVertex(String rendererName, Vertex vertex) {
            this.vertex = vertex;
            this.rendererName = rendererName;
        }

        @Override
        public String toString() {
            return rendererName + ": " + vertex.toString();
        }
    }
}
