package com.team2073.common.objective;

public interface ObjectivePrecondition {

	boolean isSafe();
	
	static ObjectivePrecondition named(String preconditionName, ObjectivePrecondition precondition) {
		return new NamedObjectivePrecondition(preconditionName, precondition);
	}
	
	class NamedObjectivePrecondition implements ObjectivePrecondition {

		private String name;
		private ObjectivePrecondition precondition;
		
		public NamedObjectivePrecondition(String name, ObjectivePrecondition precondition) {
			this.name = name;
			this.precondition = precondition;
		}

		@Override
		public boolean isSafe() {
			return precondition.isSafe();
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
