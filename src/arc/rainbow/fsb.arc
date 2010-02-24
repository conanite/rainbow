(java-import javax.swing.tree.TreeModel)
(require-lib "rainbow/swing")
(require-lib "rainbow/welder")

(= tagged-writers!tree-node (fn (node)
  (let node-name (node!name)
    (if (find node-name ())
        node-name
        (last:tokens node-name #\/)))))

(assign pb-cache (table))

(def dir-name (path)
  (apply string (intersperse "/" (rev:cdr:rev:tokens path #\/))))

(def make-tree-node (root isleaf kidfn)
  (or= pb-cache.root
    (let children nil
      (annotate 'tree-node (make-obj
        (invalidate  ()      (wipe children))
        (name        ()      root)
        (kids        ()      (or= children (kidfn)))
        (nth         (index) ((kids) index))
        (count       ()      (aif (kids) (len it) 0))
        (leaf        ()      isleaf)
        (child-index     (child)
          (index-of child (map rep (kids)))))))))

(def make-node (root)
  (make-tree-node root
                  (no:dir-exists root)
                  (if dir-exists.root
                      (fn () (map [make-node (+ root "/" _)]
                                  (dir root)))
                      nilfn)))

(def pb-tree-model ((o path "."))
  (withs (root-node (make-node path)
          listeners nil)
    (TreeModel implement t (make-obj
      (equals                  (other)          nil)
      (getRoot                 ()               root-node)
      (getChild                (node index)     (rep.node!nth index))
      (getChildCount           (node)           (rep.node!count))
      (isLeaf                  (node)           (rep.node!leaf))
      (valueForPathChanged     (tree-path node) nil)
      (getIndexOfChild         (parent child)   (rep.parent!child-index rep.child))
      (addTreeModelListener    (listener)       (push listener listeners))
      (removeTreeModelListener (listener)       (zap [rem listener _] listeners))))))

(def refresh-fsb (tree root)
  (with (expanded-paths (tree 'getExpandedDescendants (tree 'getPathForRow 0))
         selection-paths tree!getSelectionPaths)
    (tree 'setModel (pb-tree-model root))
    (each (path node) pb-cache (rep.node!invalidate))
    (while expanded-paths!hasMoreElements
      (tree 'expandPath expanded-paths!nextElement))
    (tree 'addSelectionPaths selection-paths))
    tree!grabFocus)

(def delete-file (node)
  (when node (rmfile (node!name))))

(def insert-new-file (node)
  (if (dir-exists (node!name))
      (aif (prompt "new file name:")
           ((File new "#((node!name))/#(it)") 'createNewFile))))
 
(def insert-new-dir (node)
  (if (dir-exists (node!name))
      (aif (prompt "new directory name:")
           ((File new "#((node!name))/#(it)") 'mkdir))))
 
(def rename-file (node)
  (aif (prompt "rename file to:")
       (let root (node!name)
         (mvfile "#(root)" "#(dir-name.root)/#(it)"))))

(def selected-node (tree)
  (rep tree!getSelectionPath!getLastPathComponent))

(def fsb ((o root "."))
  (withs (f (frame 20 100 320 800 "browser")
          tree     (java-new "javax.swing.JTree")
          renderer (bean "javax.swing.JLabel")
          model    (pb-tree-model root)
          sc       (scroll-pane tree colour-scheme!background)
          refreshfully (fn (f) (f:selected-node tree)
                               (refresh-fsb tree root)))
    (tree 'setModel model)
    (on-key-press tree
      'enter      (welder (((selected-node tree) 'name)))
      'delete     (refreshfully delete-file)
      'space      (refreshfully rename-file)
      'meta-n     (refreshfully insert-new-file)
      'meta-d     (refreshfully insert-new-dir)
      'f5         (refresh-fsb tree root))
    (f 'add sc)
    f!show))
