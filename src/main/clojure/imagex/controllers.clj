(ns imagex.controllers
  (:import
    (javax.imageio ImageIO)
    (javax.swing ImageIcon JList)
    (java.awt Color)
    (org.imgscalr Scalr)
    (java.awt.event ActionListener ActionEvent)
    (marvin.gui MarvinImagePanel)
    (marvin.image MarvinImage MarvinImageMask)
    (marvin.util MarvinPluginLoader)
    (viewa.util ComponentFinder)
    (viewa.view ViewContainer)))

;; *****************************************************
;; ********************* HELPERS ***********************
;; *****************************************************

(defn locate-view
  [view-seed view-name]
  "Gets any view starting having the current one as start point"
    (->
      (.getApplication view-seed)
      (.getViewManager)
      (.getViews)
      (.get view-name)))

(defn get-component-in
  [current view-name component-type component-name]
  "Retrieves any component in any view"
  (->
    (ComponentFinder/find component-type)
    (.in (locate-view current view-name))
    (.named component-name)))

(defn get-image-panel
  "Locates and returns the image panel"
  [view]
  (get-component-in view "image-processing-view-id" MarvinImagePanel "imagePanel"))

(defn get-background-list
  [view]
  (get-component-in view "image-bank-view-id" JList "backgroundImagesList"))

(defn get-selected-background
  [view]
  (.getSelectedValue (get-background-list view)))

(defn get-foreground-list
  [view]
  (get-component-in view "image-bank-view-id" JList "foregroundImagesList"))

(defn get-selected-foreground
  [view]
  (.getSelectedValue (get-foreground-list view)))

(defn load-plugin
  [short-name]
  (case short-name
    "combine" (MarvinPluginLoader/loadImagePlugin "org.marvinproject.image.combine.combineByMask.jar")
    "skin" (MarvinPluginLoader/loadImagePlugin "org.marvinproject.image.color.skinColorDetection.jar")
    "sepia" (MarvinPluginLoader/loadImagePlugin "org.marvinproject.image.color.sepia.jar")
    "gray" (MarvinPluginLoader/loadImagePlugin "org.marvinproject.image.color.gray.jar")
    "invert" (MarvinPluginLoader/loadImagePlugin "org.marvinproject.image.color.invert.jar")))

(defn to-marvin-image
  [image-icon]
  (MarvinImage. (.getImage image-icon)))

;; *****************************************************
;; *************** VIEWA CONTROLLERS *******************
;; *****************************************************

(gen-class
  :name imagex.controllers.ImageProcessingController
  :extends viewa.controller.AbstractActionController
  :prefix "process-"
  :methods [
    [postHandlingView [viewa.view.ViewContainer java.awt.event.ActionEvent] void] ])

(defn rescale
  [image size]
  (Scalr/resize image size (seq [])))

(defn extract-skin
  [input output]
  (let [plugin (load-plugin "skin")]
    (.process plugin input output nil nil false)
    output))

(defn combine
  [background foreground output]
  (let [plugin (load-plugin "combine")
        attributes (.getAttributes plugin)]
    (doto attributes
      (.set "xi" (int 100))
      (.set "yi" (int 100))
      (.set "colorMask" (.getTransparency (Color. 0 0 0)))
      (.set "combinationImage" foreground))
    (.process plugin background output nil nil false)))

(defn get-max-width
  [& images]
    (apply max (map (fn [img] (.getWidth img)) images)))

(defn get-max-height
  [& images]
    (apply max (map (fn [img] (.getHeight img)) images)))

(defn get-combined-image-holder
  [image1 image2]
  (let [width (get-max-width image1 image2)
        height(get-max-height image1 image2)]
    (MarvinImage. height width)))

(defn process-postHandlingView
  [this view event]
  (let [background (to-marvin-image (get-selected-background view))
        foreground (to-marvin-image (get-selected-foreground view))
        image-panel (get-image-panel view)
        image-out (get-combined-image-holder background foreground) ]
        (let [skin-image (extract-skin foreground image-out)
              image-out (get-combined-image-holder skin-image image-out)]
              ;combination (combine background skin-image image-out)]
          (.setImage image-panel skin-image))))

;; *****************************************************
;; *************** VIEWA LISTENERS *********************
;; *****************************************************

(gen-class
  :name imagex.controllers.ImageLoaderListener
  :prefix "image-loader-listener-"
  :extends viewa.view.event.DefaultViewContainerEventController)

(defn list-files-from
  [file-name]
  (filter
    (fn [x] (not (.isDirectory x)))
    (file-seq (clojure.java.io/file file-name))))

(defn to-image-icon
  [file]
  (ImageIcon. (rescale (ImageIO/read file) 400)))

(defn map-files-to-images
  [files]
  (map to-image-icon files))

(defn load-background-images
  []
  (map-files-to-images (list-files-from "/home/mario/Pictures/test/background/")))

(defn load-face-images
  []
  (map-files-to-images (list-files-from "/home/mario/Pictures/test/faces/")))

(defn add-to-model
  [model element]
  (.addElement model element))

(defn image-loader-listener-onViewInitUIState
  [this viewContainerEvent]
  (let [view (.getSource viewContainerEvent)
      background-image-model (.getModel (get-background-list view))
      foreground-image-model (.getModel (get-foreground-list view))
      background-files (load-background-images)
      face-files (load-face-images)
      add-background-image (partial add-to-model background-image-model)
      add-foreground-image (partial add-to-model foreground-image-model)]
        (doseq [x background-files]
          (add-background-image x))
        (doseq [x face-files]
          (add-foreground-image x))))


