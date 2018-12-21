package com.team2073.common.smartdashboard;

import java.util.LinkedList;
import java.util.List;

public class SmartDashboardAwareRegistry {
	private final List<SmartDashboardAware> instanceList = new LinkedList<>();

	public void registerInstance(SmartDashboardAware instance) {
		instanceList.add(instance);
	}

	public void updateAll() {
		instanceList.forEach(SmartDashboardAware::updateSmartDashboard);
	}

	public void readAll() {
		instanceList.forEach(SmartDashboardAware::readSmartDashboard);
	}
}
