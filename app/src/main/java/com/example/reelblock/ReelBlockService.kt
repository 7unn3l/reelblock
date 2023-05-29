package com.example.reelblock

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.app.ActivityManager

class ReelBlockService : AccessibilityService() {

    private var counter = 0

    override fun onInterrupt() {
        // This function is called when the system wants to interrupt
        // the feedback your service is providing, usually in response
        // to a user action such as moving focus to a different control.
        // Implementations should interrupt any ongoing speech and any
        // other feedback.
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // check contentDescription of the source node

        event.packageName?.let { packageName ->
            println("----------------------------------------------------------------")
            if (packageName.toString().lowercase().contains("instagram")) {
                //println("${AccessibilityEvent.eventTypeToString(event.eventType)} : ${event.contentDescription}");
                if (event.eventType == AccessibilityEvent.TYPE_VIEW_SELECTED && event.contentDescription?.toString()?.lowercase()?.contains("reel") ?: false ) {
                    killInstagramApp();
                }
                else if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED && event.contentDescription?.toString()?.lowercase()?.contains("reel von") ?: false){
                    killInstagramApp();
                }else{

                    event.source?.let { nodeInfo ->
                        // get class name of the source view
                        val className = nodeInfo.className
                        println("Class name: $className")

                        // get text of the source view
                        val text = nodeInfo.text
                        println("Text: $text")

                        // get content description of the source view
                        val contentDescription = nodeInfo.contentDescription
                        println("Content description: $contentDescription")

                        // get view ID resource name of the source view
                        val viewIdResourceName = nodeInfo.viewIdResourceName
                        println("View ID resource name: $viewIdResourceName")

                        // get parent of the source view
                        val parent = nodeInfo.parent
                        println("Parent: $parent")

                        // get children of the source view
                        for (i in 0 until nodeInfo.childCount) {
                            val child = nodeInfo.getChild(i);


                            if (child.text != null){
                                if (child.text.contains("Anzahl der Kommentare:")){
                                    killInstagramApp()
                                }
                            }

                            val description = child.contentDescription?.toString()
                            if (description != null && description == "Reels") {
                                println("[$i]type that had child with Reels: ${AccessibilityEvent.eventTypeToString(event.eventType)}")
                                //killInstagramApp()
                            }

                        }
                    }
                }
        }}
        println("----------------------------------------------------------------")
    }

    private fun findReelInViewHierarchy(nodeInfo: AccessibilityNodeInfo): Boolean {
        val contentDescription = nodeInfo.contentDescription?.toString()?.lowercase()
        if (contentDescription != null && "reel" in contentDescription) {
            //println(counter)
            //println(nodeInfo)
            return true
        }

        for (i in 0 until nodeInfo.childCount) {
            val childNodeInfo = nodeInfo.getChild(i)
            if (childNodeInfo != null) {
                if (findReelInViewHierarchy(childNodeInfo)) {
                    return true
                }
            }
        }

        return false
    }

    private fun killInstagramApp() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }
}
