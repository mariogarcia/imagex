(ns imagex.core
  (:gen-class)
  (:import
    (com.jgoodies.looks.plastic PlasticLookAndFeel)
    (com.jgoodies.looks.plastic.theme BrownSugar)
    (java.awt GradientPaint)
    (javax.swing UIManager)
    (javax.swing.plaf ColorUIResource)
    (org.jdesktop.swingx.plaf UIManagerExt PainterUIResource)
    (org.jdesktop.swingx.painter MattePainter)
    (imagex.views ImageBankView MyApplicationRootView ImageProcessingView)
    (viewa.annotation Views View ViewsPerspective)
    (viewa.view.perspective PerspectiveConstraint)
    (viewa.docking.mydoggy MyDoggyPerspective)
    (viewa.widget.view AboutView)
    (viewa.core DefaultApplicationLauncher)))

(gen-class
  :name imagex.core.ImagexLauncher
  :extends viewa.core.DefaultApplicationLauncher
  :prefix "launcher-")

(defn launcher-modify-title-panels
  "Fixes JXTitledPanel LnF"
  []
  (let [primary-color (ColorUIResource. 0 0 0)
        lighter-color (.brighter primary-color)
        gradient (GradientPaint. 0 0 primary-color 0 1 lighter-color)
        matte-painter (MattePainter. gradient true)
        painter-resource (PainterUIResource. matte-painter)]
        (UIManager/put "JXTitledPanel.titlePainter" painter-resource)))

(defn launcher-getLookAndFeel
  [this]
    (do
      (PlasticLookAndFeel/setPlasticTheme (BrownSugar.))
      (launcher-modify-title-panels)
      (PlasticLookAndFeel.)))

(gen-class
  :name
    ^{Views [
        (View {:type ImageBankView :position PerspectiveConstraint/LEFT})
        (View {:type ImageProcessingView :position PerspectiveConstraint/RIGHT})
        (View {:type MyApplicationRootView :isRoot true})]}
    ;;^{ViewsPerspective {:value MyDoggyPerspective} }
    imagex.core.MyApplication
  :extends viewa.core.DefaultApplication)

(defn -main
  []
  (let [app imagex.core.MyApplication
        launcher (imagex.core.ImagexLauncher.)]
    (.execute launcher app)))

