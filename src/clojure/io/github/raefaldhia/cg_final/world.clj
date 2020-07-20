(ns clojure.io.github.raefaldhia.cg_final.world
    (:import [com.jogamp.opengl GL2]
             [com.jogamp.opengl.util.texture TextureIO]
             [java.io File])
    (:require [clojure.io.github.raefaldhia.cg_final.map_entry :refer [map-entry]]
              [clojure.io.github.raefaldhia.cg_final.world.model :as Model]
              [clojure.io.github.raefaldhia.cg_final.world.object :as Object]))

(declare -create
         -get-model
         -get-object
         -init
         -init-models
         -is-point-in-boundary?
         -put-model
         -put-object
         -remove-model
         -remove-object
         -render
         -render-model
         -update)

(defn -create
      []
      {:objects {}
       :models (merge {} (Model/-create "spaceship-0"))
       :sky-texture nil})

(defn -init
      [world gl glu]
      (-> world
          (assoc :sky-texture (-> (File. "./src/resource/model/stars.jpg")
                                  (TextureIO/newTexture true)))
          (-init-models gl glu)))

(defn -init-models
      [world gl glu]
      (loop [world            world
             [model & models] (:models world)]
            (if (nil? model)
                world
                (let [world (let [[model world] (Model/-init model
                                                             (-remove-model world (key model))
                                                             gl glu)]
                                 (if (nil? model)
                                     world
                                     (-put-model world model)))]
                     (recur world models)))))

(defn -is-point-in-boundary?
      "Check whether a point 3D is in world boundary"
      [[x y z]]
      (and (<= (- 10) x 10)
           (<= (- 20) y 20)
           (<= (- 10) z 10)))

(defn -get-model
      [world id]
      (Model/-create id (get-in world [:models id])))

(defn -get-object
      [world id]
      (map-entry id (get-in world [:objects id])))

(defn -put-model
      [world model]
      (update world :models merge model))

(defn -put-object
      [world object]
      (update world :objects merge object))

(defn -remove-model
      [world id]
      (update world :models dissoc id))

(defn -remove-object
      [world id]
      (update world :objects dissoc id))

(defn -render-model
      [world id gl glu]
      (Model/-render (-get-model world id) world gl glu))

(defn -render
      [world gl glu]
      (.glPushMatrix gl)
      (.enable (:sky-texture world) gl)
      (.bind (:sky-texture world) gl)
      (.setTexParameteri (:sky-texture world) gl GL2/GL_TEXTURE_MIN_FILTER GL2/GL_LINEAR)
      (.setTexParameteri (:sky-texture world) gl GL2/GL_TEXTURE_MAG_FILTER GL2/GL_LINEAR)
      (.glBegin gl GL2/GL_QUADS)
      (.glTexCoord2f gl 0.5 0.5)
      (.glVertex3f gl 10 10 (- 10))
      (.glTexCoord2f gl 0.2 0.5);
      (.glVertex3f gl (- 10) 10 (- 10))
      (.glTexCoord2f gl 0.2 0.2)
      (.glVertex3f gl (- 10) (- 10) (- 10))
      (.glTexCoord2f gl 0.5 0.2)
      (.glVertex3f gl 10 (- 10) (- 10))
      (.glEnd gl)
      (.glBegin gl GL2/GL_QUADS)
      (.glTexCoord2f gl 0.5 0.5)
      (.glVertex3f gl 10 10 10)
      (.glTexCoord2f gl 0.2 0.5);
      (.glVertex3f gl (- 10) 10 10)
      (.glTexCoord2f gl 0.2 0.2)
      (.glVertex3f gl (- 10) 10 (- 10))
      (.glTexCoord2f gl 0.5 0.2)
      (.glVertex3f gl 10 10 (- 10))
      (.glEnd gl)
      (.disable (:sky-texture world) gl)
      (.glPopMatrix gl)
      (doseq [object (:objects world)]
             (.glPushMatrix gl)
             (Object/-render object world gl glu)
             (.glPopMatrix gl)))

(defn -update
      [world events dt]
      (loop [world      world
             objects-id (keys (:objects world))]
            (let [[object-id & objects-id] objects-id]
                 (if (nil? object-id)
                     world
                     (let [world (let [[object world] (Object/-update (-get-object world object-id)
                                                                      (-remove-object world object-id)
                                                                      events dt)]
                                      (if (nil? object)
                                          world
                                          (-put-object world object)))]
                          (recur world
                                 (keep #(some-> (find (:objects world) %)
                                                (key))
                                       objects-id)))))))