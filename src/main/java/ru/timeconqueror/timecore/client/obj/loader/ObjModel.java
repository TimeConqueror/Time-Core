package ru.timeconqueror.timecore.client.obj.loader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.obj.model.AbstractObjModel;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelRenderer;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ObjModel extends AbstractObjModel {
    public List<ObjModelRenderer> parts;
    private List<ObjModelRenderer> duplications = new ArrayList<>();

    ObjModel(List<ObjModelRenderer> parts) {
        this.parts = parts;
    }

    ObjModel() {
    }

    @Override
    public List<ObjModelRenderer> getParts() {
        return parts;
    }

    void setParts(List<ObjModelRenderer> renderers) {
        parts = renderers;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderAll(float scale) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            part.render(scale);
        }
    }

    /**
     * Removes all generated duplications, which will appear if you add children to other {@link ObjModelRenderer}s.
     * You may separate model parts and add children during for example constructing model.
     * Example can be seen here: {@link example.ModelPhoenix}
     * <p>
     * If you forget to clear duplications, error messages will be printed to console every render frame.
     * <p>
     * MUST be called AFTER adding children to other {@link ObjModelRenderer}s.
     * MUST NOT be called while passing {@link #getParts()}, because it will throw {@link ConcurrentModificationException};
     */
    @Override
    public void clearDuplications() throws ConcurrentModificationException {
        try {
            for (ObjModelRenderer renderer : duplications) {
                parts.remove(renderer);
            }
        } catch (ConcurrentModificationException e) {
            throw new ConcurrentModificationException("You must clear duplications ONLY AFTER passing ObjModelRaw#parts!!!\n" + e.getMessage());
        }

        duplications.clear();
    }

    @Override
    public boolean hasDuplications() {
        return !duplications.isEmpty();
    }

    private String[] formDuplicationList() {
        String[] list = new String[duplications.size()];
        for (int i = 0; i < duplications.size(); i++) {
            list[i] = duplications.get(i).getName();
        }

        return list;
    }

    @Override
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

    @Override
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

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPart(float scale, String partName) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            if (partName.equalsIgnoreCase(part.getName())) {
                part.render(scale);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPart(float scale, ObjModelRenderer partIn) {
        checkForNoDuplications();
        for (ObjModelRenderer part : parts) {
            if (part.equals(partIn)) {
                part.render(scale);
            }
        }
    }

    /**
     * Renders all parts except given. If excluded part has children, they will be counted as excluded (but it won't work if you hadn't cleared duplications through {@link #clearDuplications()}).
     */
    @Override
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

    private boolean isExcepted(ObjModelRenderer part, ObjModelRenderer[] excludedList) {
        for (ObjModelRenderer excludedPart : excludedList) {
            if (part.equals(excludedPart)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void addDuplication(ObjModelRenderer renderer) {
        duplications.add(renderer);
    }

    private void checkForNoDuplications() {
        if (hasDuplications()) {
            TimeCore.logHelper.error("=============================================================");
            TimeCore.logHelper.error("Duplications were found! You must call method ObjModelRaw#clearDuplications() after adding children to renderers.");
            TimeCore.logHelper.error("Duplications:");

            for (String str : formDuplicationList()) {
                TimeCore.logHelper.error(str);
            }

            TimeCore.logHelper.error("=============================================================");
        }
    }
}
