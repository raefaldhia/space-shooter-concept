(ns clojure.io.github.raefaldhia.cg_final.spaceship
    (:require [clojure.io.github.raefaldhia.cg_final.map_entry :refer [assocme getme updateme]]
              [clojure.io.github.raefaldhia.cg_final.world :as world]
              [clojure.io.github.raefaldhia.cg_final.world.object :as Object]))

(declare -create
         -update-fn
         -render-fn)

(defn -create
      [{:keys [rotation position color] :as spaceship}]
      (-> (Object/-create spaceship)
          (assocme :rotation (if (nil? rotation) [0.0 0.0 0.0] rotation))
          (assocme :position (if (nil? position) [0.0 0.0 0.0] position))
          (assocme :color (if (nil? color) [1.0 1.0 1.0] color))
          (assocme :collision-radius 0.4)
          (updateme :render-fns conj -render-fn)
          (updateme :update-fns conj -update-fn)))

(defn- -update-fn
       [spaceship world _events _dt]
       (vector spaceship world))

(defn- -render-fn
       [spaceship world gl glu]
       (doto gl
            ((partial apply (memfn glTranslatef x y z)) (getme spaceship :position))
            (.glRotatef (get (getme spaceship :rotation) 2) 0.0 0.0 1.0)
            (.glTranslatef 0.0 (- 0.5) 0.0)
            (.glRotatef 180 0.0 1.0 0.0)
            (.glRotatef (- 90) 1.0 0.0 0.0)
            (.glScalef 0.25 0.25 0.25))
       (apply (memfn glColor3f r g b) gl (getme spaceship :color))
       (world/-render-model world :spaceship-0 gl glu))