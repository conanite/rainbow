
(def match-end (pat str)
  (is (cut str (-:len pat)) pat))

(def is-directory (path)
  (if (dir-exists path) t))

(mac lazy args `(fn () ,@args))


(def filename (file-path)
  (last:tokens file-path #\/))

(mac files (root . constraints)
  (let constrain (fn (spec file)
                     (if (is "/" (cut spec -1))
                         (and (is-directory file)
                              (is (cut spec 0 -1) (filename file)))
                         (is "*" (cut spec 0 1))
                         (or  (is-directory file)
                              (match-end (cut spec 1) file))
                         (is "*" (cut spec -1))
                         (or  (is-directory file)
                              (headmatch (cut spec 0 -1) (filename file)))
                         (is spec (filename file))))
    `(with  (except (fn specs
                        (fn (file)
                            (if (is file 'describe)
                                `(except ,@specs)
                                (all nil (map [,constrain _ file] specs)))))
             only   (fn specs
                        (fn (file)
                            (if (is file 'describe)
                                `(only ,@specs)
                                (all t   (map [,constrain _ file] specs))))))
      (find-files ,root ,@(cons '(except ".svn/" ".DS_Store") constraints)))))

(def find-files (root . constraints)
  (with (filelist  (if (dir-exists root)
                       (dir root)
                       (let fl (list root) (= root ".") fl))
         iterator  nil
         get-file  (fn (s) (string root "/" s))
         allow     (fn (result)
                       (or (no result)
                           (all t (map [_ result] constraints)))))

    (list root (afn ()
      (let return (fn (file)
                      (if (allow file)
                          file
                          (self)))
        (if iterator
            (let result (iterator)
              (if (no result)
                  (do (wipe iterator)
                      (self))
                  (return result)))
            filelist
            (let first (get-file (pop filelist))
              (while (and filelist first (no:allow first))
                (= first (get-file (pop filelist))))
              (if (no:allow first)
                  (self)
                  (dir-exists first)
                  (do (= iterator (cadr (apply find-files first constraints)))
                      (self))
                  (return first)))))))))

(mac pr-files (root . constraints)
  `(let ff (cdr (files ,root ,@constraints))
     (while (prn (ff)))))

(def make-rfn (name args body)
  `(rfn ,name ,args ,@body))

(def plugin-list (plugins)
  (apply join (map [list car._ (make-rfn car._ cadr._ (_ 3))] plugins)))

(with (projects nil
       plugins  nil)
  (mac project (name properties . targets)
    (push name projects)
    (with (flattened (table)
           tdescs    (table))
      (with (names  nil
             ttasks (table)
             tdeps  (table)
             included (table))
        (with (dep-list      (afn (target)
                                  `(,@(map self tdeps.target) ,target))
               assemble-tasks (afn (deps)
                                  (if deps `(,@(ttasks (car deps)) ,@(self:cdr deps)))))
          (each (target deps desc . tasks) targets
            (push target names)
            (= ttasks.target  tasks)
            (= tdeps.target   deps)
            (= tdescs.target  desc))
          (each target names
            (= flattened.target (assemble-tasks (dedup:flat:dep-list target))))))
      `(mac ,name (invoke (o arg2 nil))
        (with (project-name ',name
               tdescs       ,tdescs
               properties   ',properties
               flattened    ,flattened
               plugins      ',(rev plugins))
          (if (is invoke 'plugins)
              `(each plg ',plugins
                 (pr car.plg #\space cadr.plg #\newline #\tab (plg 2) #\newline #\newline))
              (is invoke 'help)
              `(do (maptable (fn (targ desc)
                             (prn "\n" targ " :\n\t" desc))
                             ,tdescs)
                   t)
              (is invoke 'properties)
              `(withs (project-name ',project-name ,@properties)
                 ,@(map (fn (_) `(prn ',(car _) "\t:\t"
                                      (let value ,(cadr _)
                                           (if (or (isa value 'num)
                                                   (isa value 'string)
                                                   (isa value 'sym)
                                                   (alist value))
                                               value
                                               "[#((type value))]"))))
                        (pair properties)))
              (is invoke 'show)
              `(each task ',(flattened arg2) 
                 (ppr task) 
                 (prn))
              `(withs (project-name ',project-name 
                       ,@(plugin-list plugins)
                       ,@properties)
                ,@(flattened invoke)))))))

  (let add-plugin (fn args (push args plugins))
    (mac ake-plugin (plgname args desc . body)
      `(,add-plugin ',plgname ',args ,desc ',body)))

  (def ake ()
    (load "build.arc")
    (prn `(,(car projects) ,@*argv*))
    (eval `(,(car projects) ,@*argv*))))

(ake-plugin delete (file-spec)
  "Delete file-spec. If file-spec is a directory, delete it and all its contents"
  (withs (delete-file      (fn (file)
                               (rmfile file))
          delete-directory (fn (directory)
                               (each f (dir directory)
                                 (delete "#(directory)/#(f)"))
                               (delete-file directory)))
    (if (dir-exists file-spec)
        (delete-directory file-spec)
        (file-exists file-spec)
        (delete-file file-spec)
        (whilet f (file-iterator)
          (delete-file f)))))

(ake-plugin js-compress ((root fileset) target)
  "Use yui-compressor to compress all files in fileset, concatenating the result in target"
  (if (file-exists target) (rmfile target))
  (w/outfile tout target
    (w/stdout tout
      (whilet f (fileset)
        (system "java -jar lib/reference/yuicompressor-2.4.2.jar --line-break 100 --type js #(f)")))
    (close tout)))

(def listify ((root fileset) (o truncate nil))
  (let mapper (if truncate [cut _ (+ 1 len.root)] idfn)
    (rev (accum p ((afn () (awhen (fileset) (p:mapper it) (self))))))))

(ake-plugin path (fileset (o sep ":") (o truncate nil))
  "Concatenate all file-paths in fileset in a single string, separated by sep. If truncate, strip the \"root\" of the fileset from each file-path"
  (apply string (intersperse sep listify.fileset.truncate)))

(def optionify (name value . others)
  "-#(name) #(value) #((if others (apply optionify others)))")

(ake-plugin javac (source-files target-dir classpath . options)
  "Compile source-files with javac"
  (withs (src-list (path source-files " ")
          opts     (if options (apply optionify options) "")
          src-path car.source-files
          cmd      "javac -sourcepath #(src-path) -cp #(classpath) -d #(target-dir) #(opts) #(src-list)")
    (system cmd)))

(def parent-file (f)
  (apply string (intersperse "/" (butlast:tokens f #\/))))

(def copy-file (srcfile dstfile)
  "delete dstfile, copy contents of scrfile to dstfile, creating parent directories of dstfile if necessary"
  (if (file-exists dstfile) (rmfile dstfile))
  (make-directory*:parent-file dstfile)
  (w/infile i srcfile
    (w/outfile o dstfile
      (whilet b readb.i (writeb b o))
      (close i o))))

(ake-plugin copy-files ((root fileset) dest)
  "Copy files in fileset to dest, recreating directory tree structure"
  (whilet src (fileset)
    (let dst (string dest (cut src len.root))
      (copy-file src dst))))

(ake-plugin jar (exploded filename)
  "Create an archive of files under exploded in filename"
  (system "jar cf #(filename) -C #(exploded) ."))
