(def fs-view (parent stringifier root)
  (let this nil
    (= this (java-implement '("javax.swing.tree.TreeNode" "java.util.Map$Entry") t (make-obj
      (dir?        ()       (if root (dir-exists root)))
      (kidz        ()       (if (dir?) (dir root)))
      (kid         (s)      (fs-view this [last:tokens _ #\/] (+ root "/" s)))
      (getChildAt  (i)      (kid ((kidz) i)))
      (getParent   ()       parent)
      (getIndex    (node)   (index-of node (kids)))
      (isLeaf      ()       (no:dir?))
      (children    ()       (j-enumeration kid (kidz)))
      (toString    ()       (stringifier root))
      (hashCode    ()       (len root))
      (equals      (other)  (is this other))
      (getValue    ()       root)
      (getChildCount     () (len (kidz)))
      (getAllowsChildren () (dir?)))))
    this))

(def tree-node (offspring)
  (let this nil
    (= this (java-implement "javax.swing.tree.TreeNode" t (make-obj
      (getChildAt           (i)     (offspring i))
      (getChildCount        ()      (len offspring))
      (getParent            ()      nil)
      (getIndex             (node)  (index-of node offspring))
      (getAllowsChildren    ()      t)
      (isLeaf               ()      nil)
      (children             ()      (j-enumeration idfn offspring))
      (toString             ()      "(arc-path)")
      (hashCode             ()      0)
      (equals               (other) (is this other)))))
    this))

(def path-browser ()
  (withs (f (frame 150 150 800 800 "Foo")
          tree (jtree (tree-node (map [fs-view nil idfn _] (arc-path))))
          sc (scroll-pane tree colour-scheme!background))
    (on-key-press tree 
      'enter (welder ((tree!getSelectionPath 'getLastPathComponent) 'getValue)))
    (f 'add sc)
    f!pack
    f!show))
