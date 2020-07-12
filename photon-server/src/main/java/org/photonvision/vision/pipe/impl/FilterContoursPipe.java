/*
 * Copyright (C) 2020 Photon Vision.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.photonvision.vision.pipe.impl;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.photonvision.common.util.math.MathUtils;
import org.photonvision.common.util.numbers.DoubleCouple;
import org.photonvision.vision.frame.FrameStaticProperties;
import org.photonvision.vision.opencv.Contour;
import org.photonvision.vision.pipe.CVPipe;

public class FilterContoursPipe
        extends CVPipe<List<Contour>, List<Contour>, FilterContoursPipe.FilterContoursParams> {

    List<Contour> m_filteredContours = new ArrayList<>();

    @Override
    protected List<Contour> process(List<Contour> in) {
        m_filteredContours.clear();
        for (Contour contour : in) {
            filterContour(contour);
        }
        return m_filteredContours;
    }

    private void filterContour(Contour contour) {
        // Area Filtering.
        double contourArea = contour.getArea();
        double areaRatio = (contourArea / params.getFrameStaticProperties().imageArea);
        double minArea = MathUtils.sigmoid(params.getArea().getFirst());
        double maxArea = MathUtils.sigmoid(params.getArea().getSecond());
        if (areaRatio < minArea || areaRatio > maxArea) return;

        // Extent Filtering.
        RotatedRect minAreaRect = contour.getMinAreaRect();
        double minExtent = params.getFullness().getFirst() * minAreaRect.size.area() / 100;
        double maxExtent = params.getFullness().getSecond() * minAreaRect.size.area() / 100;
        if (contourArea <= minExtent || contourArea >= maxExtent) return;

        // Aspect Ratio Filtering.
        Rect boundingRect = contour.getBoundingRect();
        double aspectRatio = (double) boundingRect.width / boundingRect.height;
        if (aspectRatio < params.getRatio().getFirst() || aspectRatio > params.getRatio().getSecond())
            return;

        m_filteredContours.add(contour);
    }

    public static class FilterContoursParams {
        private final DoubleCouple m_area;
        private final DoubleCouple m_ratio;
        private final DoubleCouple m_fullness;
        private final FrameStaticProperties m_frameStaticProperties;

        public FilterContoursParams(
                DoubleCouple area,
                DoubleCouple ratio,
                DoubleCouple extent,
                FrameStaticProperties camProperties) {
            this.m_area = area;
            this.m_ratio = ratio;
            this.m_fullness = extent;
            this.m_frameStaticProperties = camProperties;
        }

        public DoubleCouple getArea() {
            return m_area;
        }

        public DoubleCouple getRatio() {
            return m_ratio;
        }

        public DoubleCouple getFullness() {
            return m_fullness;
        }

        public FrameStaticProperties getFrameStaticProperties() {
            return m_frameStaticProperties;
        }
    }
}
