package de.chribi.predictable.di;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import de.chribi.predictable.startscreen.StartScreenActivity;

@Subcomponent(modules = StartScreenActivityModule.class)
interface StartScreenActivitySubcomponent extends AndroidInjector<StartScreenActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<StartScreenActivity> {}
}
