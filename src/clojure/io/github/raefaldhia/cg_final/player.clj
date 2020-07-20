(ns clojure.io.github.raefaldhia.cg_final.player
    (:import [com.jogamp.newt.event KeyEvent])
    (:require [clojure.io.github.raefaldhia.cg_final.map_entry :refer [assocme getme updateme]]
              [clojure.io.github.raefaldhia.cg_final.keyboard :as Keyboard]
              [clojure.io.github.raefaldhia.cg_final.missile :as Missile]
              [clojure.io.github.raefaldhia.cg_final.spaceship :as Spaceship]
              [clojure.io.github.raefaldhia.cg_final.world :as World]))

(declare -create
         -update-fn
         -update-position
         -render-fn)

(defn -create
      [{:keys [_rotation _position firing-charge render-fns update-fns] :as player}]
      (-> (Spaceship/-create player)
          (assocme :firing-charge (if (nil? firing-charge) 1 0))
          (assocme :firing-rate 3)
          (assocme :color [0.0 0.0 1.0])
          (assocme :velocity [8.0 0.0 0.0])
          (cond-> (nil? render-fns) (updateme :render-fns conj -render-fn)
                  (nil? update-fns) (updateme :update-fns conj -update-fn))))

(defn- -update-fn
       [player world events dt]
       (let [keyboard-event (:keyboard events)
             player (-> player
                        (cond-> (Keyboard/-get-state keyboard-event KeyEvent/VK_A) (-update-position dt -)
                                (Keyboard/-get-state keyboard-event KeyEvent/VK_D) (-update-position dt +)
                                (< (getme player :firing-charge) 1) (updateme :firing-charge (partial + (* dt (* (/ 1 1000) (getme player :firing-rate)))))
                                (and (Keyboard/-get-state keyboard-event KeyEvent/VK_SPACE) (>= (getme player :firing-charge) 1)) (assocme :firing-charge 0)))
             world (-> world
                       (cond-> (and (Keyboard/-get-state keyboard-event KeyEvent/VK_SPACE)
                                    (= (getme player :firing-charge) 0))
                               (World/-put-object (Missile/-create {:position (update-in (getme player :position) [1] + (* (getme player :collision-radius) 2))
                                                                    :target (assoc (getme player :position) 1 20)}))))]
            (vector player world)))

(defn- -update-position
       [player dt f]
       (updateme player :position
                        (fn [position]
                            (let [new-position (mapv f position (map (partial * (/ dt 1000)) (getme player :velocity)))]
                                 (if (World/-is-point-in-boundary? new-position)
                                     new-position
                                     position)))))

(defn- -render-fn [_player _world _gl _glu])