(ns clojure.io.github.raefaldhia.cg_final.world.model
    (:import [com.jogamp.opengl GL2]
             [com.jogamp.opengl.fixedfunc GLPointerFunc]
             [de.javagl.obj ObjData ObjReader ObjUtils]
             [java.io FileInputStream]
             [java.nio IntBuffer]))

(declare -create
         -get-normals
         -get-texcoords
         -get-vertices
         -init
         -load-buffer
         -load-array-buffer
         -load-element-array-buffer
         -render)

(defn -create
      ([id]
       (-create id nil))
      ([id value]
       (first {(keyword id) value})))

(defn- -get-normals
       [model]
       (get (val model) 0))

(defn- -get-texcoords
       [model]
       (get (val model) 1))

(defn- -get-vertices
       [model]
       (get (val model) 2))

(defn -init
      [model world gl _glu]
      (let [obj (-> (FileInputStream. (str "./src/resource/model/" (-> (key model) (name)) ".obj"))
                    (ObjReader/read)
                    (ObjUtils/convertToRenderable))]
           (vector (-create (key model)
                            (vector (-load-array-buffer gl (ObjData/getNormals obj))
                                    (-load-array-buffer gl (ObjData/getTexCoords obj 2))
                                    (vector (-load-array-buffer gl (ObjData/getVertices obj))
                                            (-load-element-array-buffer gl (ObjData/getFaceVertexIndices obj)))))
                   world)))

(defn- -load-buffer
       [gl array type unit]
       (let [buffer (IntBuffer/allocate 1)
             array-capacity (.capacity array)]
            (doto gl
                  (.glGenBuffers 1 buffer)
                  (.glBindBuffer type (.get buffer 0))
                  (.glBufferData type (* array-capacity unit) array GL2/GL_STATIC_DRAW)
                  (.glBindBuffer type 0))
            (vector (.get buffer 0) array-capacity)))

(defn- -load-array-buffer
      [gl array]
      (-load-buffer gl array GL2/GL_ARRAY_BUFFER (Float/BYTES)))

(defn- -load-element-array-buffer
      [gl array]
      (-load-buffer gl array GL2/GL_ELEMENT_ARRAY_BUFFER (Integer/BYTES)))

(defn -render
      [model _world gl _glu]
      (let [[[vertices-buffer _]
             [face-vertex-indices-buffer
              face-vertex-indices-capacity]] (-get-vertices model)]
           (doto gl
                 (.glEnableClientState GLPointerFunc/GL_VERTEX_ARRAY)
                 (.glEnableClientState GLPointerFunc/GL_NORMAL_ARRAY)
                 (.glBindBuffer GL2/GL_ARRAY_BUFFER vertices-buffer)
                 (.glVertexPointer 3 GL2/GL_FLOAT 0 0)
                 (.glBindBuffer GL2/GL_ELEMENT_ARRAY_BUFFER face-vertex-indices-buffer)
                 (.glDrawElements GL2/GL_TRIANGLES face-vertex-indices-capacity GL2/GL_UNSIGNED_INT 0)
                 (.glDisableClientState GLPointerFunc/GL_NORMAL_ARRAY)
                 (.glDisableClientState GLPointerFunc/GL_VERTEX_ARRAY)
                 (.glBindBuffer GL2/GL_ARRAY_BUFFER 0)
                 (.glBindBuffer GL2/GL_ELEMENT_ARRAY_BUFFER 0))))