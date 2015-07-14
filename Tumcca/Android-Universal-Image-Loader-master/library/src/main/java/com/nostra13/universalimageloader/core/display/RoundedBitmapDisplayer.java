/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.display;

import android.graphics.*;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Can display bitmap with rounded corners. This implementation works only with ImageViews wrapped
 * in ImageViewAware.
 * <br />
 * This implementation is inspired by
 * <a href="http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/">
 * Romain Guy's article</a>. It rounds images using custom drawable drawing. Original bitmap isn't changed.
 * <br />
 * <br />
 * If this implementation doesn't meet your needs then consider
 * <a href="https://github.com/vinc3m1/RoundedImageView">RoundedImageView</a> or
 * <a href="https://github.com/Pkmmte/CircularImageView">CircularImageView</a> projects for usage.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public class RoundedBitmapDisplayer implements BitmapDisplayer {

	protected final int cornerRadius;
	protected final int margin;


	public RoundedBitmapDisplayer(int cornerRadiusPixels) {
		this(cornerRadiusPixels, 0);
	}

	public RoundedBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
		this.cornerRadius = cornerRadiusPixels;
		this.margin = marginPixels;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}

		imageAware.setImageDrawable(new RoundedDrawable(bitmap, cornerRadius, margin, imageAware.getScaleType()));
	}

	public static class RoundedDrawable extends Drawable {

		protected final float cornerRadius;
		protected final int margin;

		protected final RectF mRect = new RectF(),
				mBitmapRect;
		protected final BitmapShader bitmapShader;
		protected final Paint paint;
		protected ViewScaleType mScaleType;

		public RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin, ViewScaleType scaleType) {
			this.cornerRadius = cornerRadius;
			this.margin = margin;

			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapRect = new RectF (margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
			mScaleType = scaleType;
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			float height = bounds.height();
			if(bounds.height()<=1){
				height = bounds.width()/mBitmapRect.width()*mBitmapRect.height();
			}
			mRect.set(margin, margin, bounds.width() - margin, height - margin);
			
			// Resize the original bitmap to fit the new bound
			Matrix shaderMatrix = new Matrix();
			if(mScaleType==ViewScaleType.CENTER_CROP){
				float dx = 0;
				float dy = 0;
				float scale = 1;
				if (mBitmapRect.width() * mRect.height() > mRect.width() * mBitmapRect.height()) {
					scale = mRect.height() / (float) mBitmapRect.height();
					dx = (mRect.width() - mBitmapRect.width() * scale) * 0.5f;
				} else {
					scale = mRect.width() / (float) mBitmapRect.width();
					dy = (mRect.height() - mBitmapRect.height() * scale) * 0.5f;
				}

				shaderMatrix.setScale(scale, scale);
				shaderMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
			}
			else{
				shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
			}

			bitmapShader.setLocalMatrix(shaderMatrix);
			
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}
}
