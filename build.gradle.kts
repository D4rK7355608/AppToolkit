// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(notation = libs.plugins.android.application) apply false
    alias(notation = libs.plugins.kotlin.android) apply false
    alias(notation = libs.plugins.android.library) apply false
    alias(notation = libs.plugins.compose.compiler) apply false
}