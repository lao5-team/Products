package com.pineapple.mobilecraft.domain.activity;

import java.util.ArrayList;
import java.util.List;

import com.pineapple.mobilecraft.data.ActivityData;

/**
 * @author yh
 *
 */
public interface IActivityLoader {
	List<ActivityData> getActivityList();
}
