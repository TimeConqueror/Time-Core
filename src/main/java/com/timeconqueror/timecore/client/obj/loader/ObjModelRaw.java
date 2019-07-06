package com.timeconqueror.timecore.client.obj.loader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static com.timeconqueror.timecore.TimeCore.logger;

public class ObjModelRaw {
    public List<ObjModelRenderer> parts;
    private List<ObjModelRenderer> duplications = new ArrayList<>();
    private String name;

    ObjModelRaw(List<ObjModelRenderer> parts) {
        this.parts = parts;
    }

    ObjModelRaw() {
    }

    public String getType() {
        return "obj";
    }

    @SideOnly(Side.CLIENT)
    public void renderAll(float scale) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            part.render(scale);
        }
    }

    /**
     * Removes all generated duplicates, which appeared after adding children to other {@link ObjModelRenderer}s.
     * MUST NOT be called while passing {@link #parts}, because it will throw {@link ConcurrentModificationException};
     */
    public void clearDuplications() throws ConcurrentModificationException {
        try {
            for (ObjModelRenderer renderer : duplications) {
                parts.remove(renderer);
            }
        } catch (ConcurrentModificationException e){
            throw new ConcurrentModificationException("You must clear duplications ONLY AFTER passing ObjModelRaw#parts!!!\n" + e.getMessage());
        }

        duplications.clear();
    }

    public boolean hasDuplications(){
        return !duplications.isEmpty();
    }

    private String[] formDuplicationList(){
        String[] list = new String[duplications.size()];
        for (int i = 0; i < duplications.size(); i++) {
            list[i] = duplications.get(i).getName();
        }

        return list;
    }

    @SideOnly(Side.CLIENT)
    public void renderOnly(float scale, String... groupNames) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            for (String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(part.getName())) {
                    part.render(scale);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderOnly(float scale, ObjModelRenderer... partsIn) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            for (ObjModelRenderer partIn : partsIn) {
                if (part.equals(partIn)) {
                    part.render(scale);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderPart(float scale, String partName) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            if (partName.equalsIgnoreCase(part.getName())) {
                part.render(scale);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderPart(float scale, ObjModelRenderer partsIn) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            if (part.equals(partsIn)) {
                part.render(scale);
            }
        }
    }

    /**
     * Renders all parts except given. If excluded part has children, they will be counted as excluded (but it won't work if you hadn't cleared duplications through {@link #clearDuplications()}).
     */
    @SideOnly(Side.CLIENT)
    public void renderAllExcept(float scale, ObjModelRenderer... excludedPartsIn) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            boolean skipPart = isExcepted(part, excludedPartsIn);

            if (!skipPart) {
                part.render(scale);
            }
        }
    }

    private boolean isExcepted(ObjModelRenderer part, ObjModelRenderer[] excludedList){
        for (ObjModelRenderer excludedPart : excludedList) {
            if (part.equals(excludedPart)) {
                return true;
            }
        }

        return false;
    }

    void setParts(List<ObjModelRenderer> renderers){
        parts = renderers;
    }

    void addDuplication(ObjModelRenderer renderer){
        duplications.add(renderer);
    }

    void setName(String name) {
        this.name = name;
    }

    private void checkForNoDuplications(){
        if(hasDuplications()){
            logger.error("=============================================================");
            logger.error("Duplications were found! You must call method ObjModelRaw#clearDuplications() after adding children to renderers.");
            logger.error("Duplications:");

            for(String str : formDuplicationList()){
                logger.error(str);
            }

            logger.error("=============================================================");
        }
    }
}
