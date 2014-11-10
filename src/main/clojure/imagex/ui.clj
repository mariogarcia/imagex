(ns imagex.ui
  (:import
    (java.awt Dimension)
    (javax.swing JSplitPane JList JScrollPane ImageIcon DefaultListCellRenderer ListSelectionModel)
    (org.jdesktop.swingx JXTitledPanel)
    (marvin.io MarvinImageIO)
    (marvin.gui MarvinImagePanel)
    (org.imgscalr Scalr)
    (org.jdesktop.swingx JXTaskPaneContainer JXTaskPane JXHyperlink)))

(gen-class
  :name imagex.ui.ThumbnailListRenderer
  :prefix "list-cell-"
  :exposes-methods {getListCellRendererComponent parentgetListCellRendererComponent}
  :extends javax.swing.DefaultListCellRenderer)

(defn resize
  [image]
  (Scalr/resize image 100 (seq [])))

(defn list-cell-getListCellRendererComponent
  [this current-list value index selected has-focus]
    (let [image (.getImage value)
          thumbnail (resize image)
          resized (ImageIcon. thumbnail)]
      (.parentgetListCellRendererComponent this current-list resized index selected has-focus)))

(defn create-image-list
  [name]
  (JScrollPane.
    (doto (JList.)
      (.setCellRenderer (imagex.ui.ThumbnailListRenderer.))
      (.setModel (javax.swing.DefaultListModel.))
      (.setFixedCellWidth 110)
      (.setVisibleRowCount -1)
      (.setSelectionMode ListSelectionModel/SINGLE_INTERVAL_SELECTION)
      (.setLayoutOrientation JList/HORIZONTAL_WRAP)
      (.setBorder (javax.swing.border.EmptyBorder. 10 10 10 10))
      (.setName name))))

(defn create-image-panel
  [name]
  (doto (JXTitledPanel.)
    (.setTitle "random")
    (.setPreferredSize (Dimension. 400 300))
    (.setName name)))

(defn background-images-panel
  []
  (let [panel (create-image-panel "backgroundImagesPanel")
        panel-list (create-image-list "backgroundImagesList")]
    (doto panel
      (.add panel-list))))

(defn foreground-images-panel
  []
  (let [panel (create-image-panel "foregroundImagesPanel")
        panel-list (create-image-list "foregroundImagesList")]
    (doto panel
      (.add panel-list))))

(defn photos-panel
  []
  (let [up (background-images-panel)
        down (foreground-images-panel)]
      (doto (JSplitPane. JSplitPane/VERTICAL_SPLIT up down)
        (.setOneTouchExpandable true)
        (.setDividerLocation 450))))

(defn image-panel
  []
  "Image panel"
  (doto (MarvinImagePanel.)
    (.setName "imagePanel")))










