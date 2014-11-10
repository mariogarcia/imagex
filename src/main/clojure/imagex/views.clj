(ns imagex.views
  (:require [imagex.ui :as ui])
  (:import
    (imagex.controllers ImageLoaderListener ImageProcessingController)
    (viewa.widget.controller ExitActionController)
    (viewa.annotation Controllers Controller Listeners Listener)
    (viewa.view DefaultViewContainerFrame)))

;; crear conjunto de funciones para manejar instancias en lugar de clases.
;; proxy --> para extender clases abstractas o hacer llamadas a super
;; para interfaces reify
(gen-class
  :name
    ^{Controllers [
        (Controller {:type ExitActionController :pattern "exit"}) ]}
    imagex.views.MyApplicationRootView
  :extends viewa.view.DefaultViewContainerFrame)

(gen-class
  :name
    ^{Listeners [
        (Listener {:type ImageLoaderListener}) ]}
    imagex.views.ImageBankView
  :init "init"
  :prefix "image-bank-"
  :constructors { [] [String java.awt.Component] }
  :extends viewa.view.DefaultViewContainer)

(defn image-bank-init []
   "Setting id and inner component"
   [ ["image-bank-view-id" (ui/photos-panel)]])

(gen-class
  :name
    ^{Controllers [
        (Controller {:type ImageProcessingController :pattern "execute"}) ]}
      imagex.views.ImageProcessingView
  :init "init"
  :prefix "image-processing-"
  :constructors { [] [String java.awt.Component] }
  :extends viewa.view.DefaultViewContainerEditor)

(defn image-processing-init []
   "Setting id and inner component"
   [ ["image-processing-view-id" (ui/image-panel)]])
