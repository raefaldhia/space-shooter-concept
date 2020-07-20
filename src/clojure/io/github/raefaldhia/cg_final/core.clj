(ns clojure.io.github.raefaldhia.cg_final.core
    (:import [com.jogamp.newt.opengl GLWindow]
             [com.jogamp.newt.event KeyListener]
             [com.jogamp.opengl GLCapabilities GLProfile GLEventListener GL2]
             [com.jogamp.opengl.glu GLU])
    (:require [clojure.io.github.raefaldhia.cg_final.engine :as Engine]
              [clojure.io.github.raefaldhia.cg_final.keyboard :as Keyboard])
    (:gen-class))

(declare -main
         -reshape)

(defn -main
      []
      (let [window      (->> (GLProfile/get GLProfile/GL2)
                             (new GLCapabilities)
                             (GLWindow/create))
            glu         (new GLU)
            -display-fn (atom (fn [_ _]))
            -init-fn    (atom (fn [_ _]))
            keyboard    (volatile! (Keyboard/-create))]
           (.setTitle window "Raefaldhi Amartya Junior - 181524026")
           (.setSize window 500 500)
           (.setVisible window true)
           (.addGLEventListener window 
                                (reify GLEventListener
                                       (dispose [_ drawable])
                                       (display [_ drawable]
                                                (@-display-fn (-> (.getGL drawable)
                                                                  (.getGL2))
                                                              glu))
                                       (init [_ drawable]
                                             (@-init-fn (-> (.getGL drawable)
                                                            (.getGL2))
                                                        glu))
                                       (reshape [_ drawable x y w h]
                                                (-reshape (-> (.getGL drawable)
                                                              (.getGL2))
                                                          x y w h))))
           (.addKeyListener window
                            (reify KeyListener
                                   (keyPressed [_ keyEvent]
                                               (vreset! keyboard
                                                        (Keyboard/-press @keyboard
                                                                         (.getKeyCode keyEvent))))
                                   (keyReleased [_ keyEvent]
                                                (when-not (.isAutoRepeat keyEvent)
                                                          (vreset! keyboard (Keyboard/-release @keyboard
                                                                                               (.getKeyCode keyEvent)))))))
           (Engine/-run (Engine/-create)
                        (fn [-new-init-fn]
                            (reset! -init-fn -new-init-fn)
                            (.display window)
                            (fn [-new-display-fn]
                                (reset! -display-fn -new-display-fn)
                                (.display window)))
                        (fn []
                            {:keyboard @keyboard}))
           (.destroy window)))

(defn -reshape
      [gl _x _y h w]
      (.glViewport gl 0 0 h w)
      (.glMatrixMode gl GL2/GL_PROJECTION)
      (.glLoadIdentity gl)
      (let [range 10.0]
           (if (<= w h)
               (.glOrtho gl (- range) range (* (- range) (/ h w)) (* range (/ h w)) (* (- range) 2) (* range 2))
               (.glOrtho gl (* (- range) (/ h w)) (* range (/ h w)) (- range) range (* (- range) 2) (* range 2))))
      (.glMatrixMode gl GL2/GL_MODELVIEW)
      (.glLoadIdentity gl))