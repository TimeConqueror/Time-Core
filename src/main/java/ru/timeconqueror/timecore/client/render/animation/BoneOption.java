package ru.timeconqueror.timecore.client.render.animation;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.TimeModel;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.List;

public class BoneOption {
    private String boneName;
    @Nullable
    private List<KeyFrame> rotations;
    @Nullable
    private List<KeyFrame> positions;
    @Nullable
    private List<KeyFrame> scales;

    public BoneOption(String boneName, @Nullable List<KeyFrame> rotations, @Nullable List<KeyFrame> positions, @Nullable List<KeyFrame> scales) {
        this.boneName = boneName;
        this.rotations = rotations;
        this.positions = positions;
        this.scales = scales;
    }

    @Nullable
    private static Pair<KeyFrame, KeyFrame> findKeyFrames(List<KeyFrame> frames, long existingTime) {
        if (frames == null) return null;

        for (int i = 0; i < frames.size(); i++) {
            KeyFrame keyFrame = frames.get(i);

            if (i == 0 && keyFrame.getStartTime() > existingTime) {
                return new Pair<>(null, keyFrame);
            } else if (i == frames.size() - 1) {
                return new Pair<>(keyFrame, null);
            }

            if (keyFrame.getStartTime() <= existingTime && frames.get(i + 1).getStartTime() > existingTime) {
                return new Pair<>(keyFrame, frames.get(i + 1));
            }
        }

        return null;
    }

    private static Vector3f calcChangeVectorFor(Animation animation, Pair<KeyFrame, KeyFrame> keyPair, float startX, float startY, float startZ, int existingTime) {
        KeyFrame start = keyPair.getA();
        KeyFrame end = keyPair.getB();

        Vector3f startVec;
        Vector3f endVec;
        int startTime;
        int endTime;

        if (start == null) {
            startVec = new Vector3f(startX, startY, startZ);
            startTime = 0;
        } else {
            startVec = start.getVec();
            startTime = start.getStartTime();
        }

        if (end == null) {
            endVec = startVec;
            endTime = animation.getLength();
        } else {
            endVec = end.getVec();
            endTime = end.getStartTime();
        }

        float factor = (existingTime - startTime) / (float) endTime;

        float outX = MathHelper.lerp(factor, startVec.getX(), endVec.getX());
        float outY = MathHelper.lerp(factor, startVec.getY(), endVec.getY());
        float outZ = MathHelper.lerp(factor, startVec.getZ(), endVec.getZ());

        return new Vector3f(outX, outY, outZ);
    }

    public void apply(Animation animation, TimeModel model, int existingTime) {
        TimeModelRenderer piece = model.getPiece(boneName);
        if (piece != null) {
            Pair<KeyFrame, KeyFrame> keyPair = findKeyFrames(rotations, existingTime);
            if (keyPair != null) {
                Vector3f rotateVector = calcChangeVectorFor(animation, keyPair, piece.rotateAngleX, piece.rotateAngleY, piece.rotateAngleZ, existingTime);
                piece.rotateAngleX = rotateVector.getX();
                piece.rotateAngleY = rotateVector.getY();
                piece.rotateAngleZ = rotateVector.getZ();
            }

            keyPair = findKeyFrames(positions, existingTime);
            if (keyPair != null) {
                Vector3f posVector = calcChangeVectorFor(animation, keyPair, piece.offsetX, piece.offsetY, piece.offsetZ, existingTime);
                piece.offsetX = posVector.getX();
                piece.offsetY = posVector.getY();
                piece.offsetZ = posVector.getZ();
            }

            keyPair = findKeyFrames(scales, existingTime);
            if (keyPair != null) {
                Vector3f vec = calcChangeVectorFor(animation, keyPair, piece.getScaleFactor().getX(), piece.getScaleFactor().getY(), piece.getScaleFactor().getZ(), existingTime);
                piece.setScaleFactor(vec.getX(), vec.getY(), vec.getZ());
            }
        } else {
            TimeCore.LOGGER.error("Can't find bone with name " + boneName + " in animation " + animation.getName() + " applied for model " + model.getName());
        }
    }
}
