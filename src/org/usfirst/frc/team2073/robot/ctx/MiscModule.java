package org.usfirst.frc.team2073.robot.ctx;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.util.inject.InjectNamedTypeListener;

public class MiscModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(CommandFactory.class));
        bindListener(Matchers.any(), new InjectNamedTypeListener());
        bind(SmartDashboardAwareRegistry.class).in(Singleton.class);
    }
}
