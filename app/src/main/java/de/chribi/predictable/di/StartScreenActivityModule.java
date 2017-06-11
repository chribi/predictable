package de.chribi.predictable.di;


import dagger.Binds;
import dagger.Module;
import de.chribi.predictable.startscreen.StartScreenActivity;
import de.chribi.predictable.startscreen.StartScreenView;

@Module
abstract class StartScreenActivityModule {
    @Binds
    abstract StartScreenView bindView(StartScreenActivity activity);
}
