package com.team2073.common.dev.config;

import java.util.ArrayList;
import java.util.List;

class ApplicationContext {
	private List<String> activeProfiles = new ArrayList<>();

	ApplicationContext() {
		activeProfiles.add("mainbot");
	}

	List<String> getActiveProfiles() {
		return activeProfiles;
	}
}
