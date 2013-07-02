
import java.util.ArrayList;
//////////////////////////////////////////////////////////////////
//Modified into an AVL Tree by Mike Hawkins
//Added:
//height variable to BSTNodeND class
//balanceLL(), balanceRL(), balanceRR(), balanceLR() - balance/rotation operations
//balancePath(), balanceFactor()
//path() - finds the entire patch of the element
//updateHeight() - updates height variable of the node after balancing
//Re-wrote remove() method
//added a balance check after remove() or insert() called
/////////////////////////////////////////////////////////////////

   public class BinarySearchTreeND 
      < K extends Comparable < ? super K > > 
      // ANY class that extends the base comparable type (K)
      // of this data structure instance may be inserted
   {
      static final int INORDER    = 1; // select toString (int)
      static final int PREORDER   = 2; // select toString (int)
      static final int POSTORDER  = 3; // select toString (int)
      static final int LEVELORDER = 4; // select toString (int)
      static final int DEPTHDELTA = 5; // used to create a text tree display
   
      public static void main (String args []) {
         BinarySearchTreeND < Integer > x = new BinarySearchTreeND < Integer > ();
         int arr [] = {40, 20, 60, 10, 30, 50, 70, 5, 15, 25, 35, 45, 55, 65, 75};
         int rem [] = {40, 45, 50, 10, 55};
         for (int y: arr) x.insert (y);
      
         System.out.println ("X:\n" + x);
         System.out.println ("X in-order:\n   "    + x.toString(INORDER));
         System.out.println ("X pre-order:\n   "   + x.toString(PREORDER));
         System.out.println ("X post-order:\n   "  + x.toString(POSTORDER));
         System.out.println ("X level-order:\n   " + x.toString(LEVELORDER));
      
         Integer t = x.find(10);
         System.out.println ("find: " + t);
         System.out.println ("find: " + x.find(20));
         System.out.println ("Size: " + x.getSize());
         System.out.println ("MAX: " + x.max());
         for (int y: rem) {
            System.out.println ("Removing: " + y);
            x.remove (y);
            System.out.println ("result:\n" + x);
         }
         System.out.println ("X:\n"  + x);
            
      
      } // end main
   
      private BSTNodeND < K > root = null;
      
      //MH:
      //altered the insert method to call
      //the balance method when necessary (in the insertValue method)
      public void insert (K d) {
         if(root == null){
             root = new BSTNodeND < K > (d);              
         }
         else{
             insertValue (d, root);             
         }                           
      } // end insert, public version
   
      public K find (K d) {
         if (root == null) 
            return null;
         BSTNodeND < K > t = findValue (d, root);
         return (t==null)?null:t.data;
      } // end find method
      
      public K max () {
         if (root == null) 
            return null;
         return findMax(root).data;
      } // end max method
   
      public int getSize () {
         return getSize (root);}
   
      //MH:
      //Changed the remove method to perform
      //balancing, if necessary.
      public void remove (K d) {
         root = remove (d, root);         
           if (root == null)
             return;
         
         BSTNodeND<K> parent = null;
         BSTNodeND<K> current = root;
         while (current != null){
             if (d.compareTo(current.data) < 0){
                 parent = current;
                 current = current.left;
             }else if(d.compareTo(current.data) > 0){
                 parent = current;
                 current = current.right;
             }else{
                 break;
             }
         }
             
             if (current == null){
                 return;
             }
             
             if (current.left == null){
                 if (parent == null){
                     root = current.right;
                 }else{
                     if (d.compareTo(parent.data) < 0){
                         parent.left = current.right;
                     }else{
                         parent.right = current.right;
                     }
                     balancePath(parent.data);
                 }
             }
             else {
                 BSTNodeND<K> parentOfRightMost = current;
                 BSTNodeND<K> rightMost = current.left;
                 
                 while (rightMost.right != null){
                     parentOfRightMost = rightMost;
                     rightMost = rightMost.right;
                 }
                 
                 current.data = rightMost.data;
                 
                 if (parentOfRightMost.right == rightMost){
                     parentOfRightMost.right = rightMost.left;
                 }else{
                     parentOfRightMost.left = rightMost.left;
                 }
                 balancePath(parentOfRightMost.data);
             }                                 
      } // end remove data
   
      public String toString () {
         if (root == null) 
            return null;
         return toString(root);}
         
      public String toString (int ord) {
         if (root == null) 
            return null;
         return toString(ord, root);}
   
      private void insertValue (K d, BSTNodeND < K > n) {
         if (d.compareTo (n.data)  > 0) 
            if (n.right == null) n.right = new BSTNodeND < K > (d, n);
            else insertValue (d, n.right);
         else 
            if (n.left == null) n.left = new BSTNodeND < K > (d, n);
            else insertValue (d, n.left);    
         balancePath(d);
      } // end method insertValue
      
      //MH:
      //Balances the patch of the provided element
      private void balancePath(K k){
          java.util.ArrayList<BSTNodeND<K>> path = path(k);
          for (int i = path.size() - 1; i >= 0; i--){
              BSTNodeND<K> A = (BSTNodeND < K >)(path.get(i));
              updateHeight(A);
              BSTNodeND < K > parentA = (A == root) ? null : (BSTNodeND < K >)(path.get(i-1));
              
              switch (balanceFactor(A)){
                  case -2:
                      if (balanceFactor((BSTNodeND < K >)A.left) <= 0){
                          balanceLL(A, parentA);
                      }else{
                          balanceLR(A, parentA);
                      }
                      break;
                  case +2:
                      if (balanceFactor((BSTNodeND < K >)A.right) >= 0){
                          balanceRR(A, parentA);
                      }else{
                          balanceRL(A, parentA);
                      }
                      break;
              }
          }
          
      }
      //MH:
      //Performs and LL balance
      private void balanceLL(BSTNodeND < K > node, BSTNodeND < K > nodeParent){
          BSTNodeND<K> B = node.left;
          if (node == root){
              root = B;
          }else{
              if (nodeParent.left == node){
                  nodeParent.left = B;
              }else{
                  nodeParent.right = B;
              }
          }          
          node.left = B.right;
          B.right = node;   
          updateHeight((BSTNodeND<K>)node);
          updateHeight((BSTNodeND<K>)B);
      }
      
      //MH:
      //Performance an LR balance
      private void balanceLR(BSTNodeND < K > node, BSTNodeND < K > nodeParent){
          BSTNodeND < K > B = node.left;
          BSTNodeND < K > C = node.right;
          
          if (node == root){
              root = C;
          }else{
              if (nodeParent.left == node){
                  nodeParent.left = C;
              }else{
                  nodeParent.right = C;
              }
          }
          
          node.left = C.right;
          B.right = C.left;
          C.left = B;
          C.right = node;   
          updateHeight((BSTNodeND<K>)node);
          updateHeight((BSTNodeND<K>)B);
          updateHeight((BSTNodeND<K>)C);
      }
      
      //MH:
      //Performs and RR balance
      private void balanceRR(BSTNodeND < K > node, BSTNodeND < K > nodeParent){
          BSTNodeND < K > B = node.right;
          
          if (node == root){
              root = B;
          }else{
              if (nodeParent.left == node){
                  nodeParent.left = B;
              }else{
                  nodeParent.right = B;
              }
          }
          
          node.right = B.left;
          B.left = node;
          updateHeight((BSTNodeND<K>)node);
          updateHeight((BSTNodeND<K>)B);
      }
      
      //MH:
      //Performs an RL balance
      private void balanceRL(BSTNodeND < K > node, BSTNodeND < K > nodeParent){
          BSTNodeND < K > B = node.right;
          BSTNodeND < K > C = B.left;
          
          if (node == root){
              root = C;
          }else{
              if (nodeParent.left == node){
                  nodeParent.left = C;
              }else{
                  nodeParent.right = C;
              }
          }
          
          node.right = C.left;
          B.left = C.right;
          C.left = node;
          C.right = B;
          updateHeight((BSTNodeND<K>)node);
          updateHeight((BSTNodeND<K>)B);
          updateHeight((BSTNodeND<K>)C);
      }
      
      //MH:
      //Gets the balance factor to determine which type of balance/rotation is needed
      private int balanceFactor(BSTNodeND < K > node){
          if (node.right == null)
              return -findHeight(node);
          else if (node.left == null)
              return +findHeight(node);
          else
              return findHeight(node.right) - findHeight(node.left);
      }
      
      private int findHeight(BSTNodeND < K > node)
      {
            if(node == null)  
                return -1;
            return node.height;
       }
      
                
      private BSTNodeND < K > findValue (K d, BSTNodeND < K > n) {
         if (n.data.compareTo(d) == 0) 
            return n;
         if (n.data.compareTo (d) > 0) 
            return (n.left==null)?null:findValue (d, n.left);
         return (n.right == null)?null:findValue(d, n.right);
      } // end findValue
      
      private BSTNodeND < K > findMax (BSTNodeND < K > n) {
         if (n.right == null) 
            return n;
         return findMax(n.right);
      } // end findValue
      
      private int getSize (BSTNodeND < K > t) {
         if (t == null) 
            return 0;
         return getSize (t.left) + getSize (t.right) + 1;
      } // end getSize node
      
      
      private BSTNodeND < K > removeRoot (BSTNodeND < K > t) {
         if (t.left  == null) {
            if (t.right != null) 
               t.right.parent = t.parent;
            return t.right;
         }
         if (t.right == null) {
            t.left.parent = t.parent; // t.left != null because of earlier if test case
            return t.left;
         }
         BSTNodeND < K > newTop = findMax(t.left);
         remove (newTop.data, t); // lose the node instance, leave tree intact
         t.data = newTop.data;    // just replace the data at the internal node
         return t;
      } // end remove data, tree
   
      private BSTNodeND < K > remove (K d, BSTNodeND < K > t) {
         if (t == null) 
            return null;
         if (d.compareTo (t.data) < 0) 
            t.left  = remove (d, t.left );
         else 
            if (d.compareTo (t.data)> 0) 
               t.right = remove (d, t.right);
            else // d equals t.data
               t = removeRoot (t);
         //balancePath(t.data);
         return t;
      } // end remove data, tree
  
      private String toString (BSTNodeND n) {
         return toTreeString (5, n); 
      } // end toString
         
      private String toTreeString (int depth, BSTNodeND n) { // depth = 0 is bad
         StringBuffer st = new StringBuffer ();
         char d = '\\';                         // default = this is right child
         if (n.parent == null) d = ' ';         // case of root
         else if (n == n.parent.left) d = '/';  // case that this is left child
         st.append ((n.left  == null)?"":toTreeString  (depth + DEPTHDELTA, n.left));
         st.append (String.format ("%" + depth + "s%s\n", d, n.data)); // ND: fixed 4/17/2009
         st.append ((n.right == null)?"":toTreeString (depth + DEPTHDELTA, n.right));
         return st.toString();
      } // end method toTreeString
         
      private String toInOrderString (BSTNodeND n) {
         StringBuffer st = new StringBuffer ();
         st.append ((n.left  == null)?"":toInOrderString(n.left));
         st.append (n.data + " ");
         st.append ((n.right == null)?"":toInOrderString(n.right));
         return st.toString();
      } // end toInOrderString
         
      private String toPreOrderString (BSTNodeND n) {
         StringBuffer st = new StringBuffer ();
         st.append (n.data + " " );
         st.append ((n.left  == null)?"":toPreOrderString(n.left));
         st.append ((n.right == null)?"":toPreOrderString(n.right));
         return st.toString();
      } // end toPreOrderString
         
      private String toPostOrderString (BSTNodeND n) {
         StringBuffer st = new StringBuffer ();
         st.append ((n.left  == null)?"":toPostOrderString(n.left));
         st.append ((n.right == null)?"":toPostOrderString(n.right));
         st.append (n.data + " ");
         return st.toString();
      } // end to PostOrderString
         
         // See: http://en.wikipedia.org/wiki/Tree_traversal
      private String toLevelOrderString (BSTNodeND n) {
         StringBuffer st = new StringBuffer ();
         BSTNodeND node;
         java.util.ArrayDeque < BSTNodeND > q 
               = new java.util.ArrayDeque < BSTNodeND > ();
         q.add (n);          // start queue by adding this (root?) to queue
         while (q.size() > 0) { 
            node = q.remove();                          // remove the head of queue
            st.append (node.data + " ");                // process head data to String
            if (node.left != null) q.add (node.left);   // insert left child at end of queue
            if (node.right != null) q.add (node.right); // insert right child at end or queue
         } // end queue processing
         return st.toString();
      } // end to LevelOrderString
         
      private String toString (int order, BSTNodeND n) {
         String st = null;
         switch (order) {
            case INORDER:    st = toInOrderString   (n); 
               break;
            case PREORDER:   st = toPreOrderString  (n); 
               break;
            case POSTORDER:  st = toPostOrderString (n); 
               break;
            case LEVELORDER: st = toLevelOrderString(n); 
               break;
         }
         return st;
      } // end toString int

    private ArrayList<BSTNodeND<K>> path(K k) {
        java.util.ArrayList<BSTNodeND<K>> list = new java.util.ArrayList<>();
        BSTNodeND<K> current = root;
        
        while (current != null){
            list.add(current);
            if (k.compareTo(current.data) < 0){
                current = current.left;
            }else if (k.compareTo(current.data) > 0){
                current = current.right;
            }else{
                break;
            }
        }
        
        return list;
    }
    
    //MH:
    //updates the new AVL variable -  height
    private void updateHeight(BSTNodeND < K > node){
        if (node.left == null && node.right == null){
            node.height = 0;
        }else if (node.left == null){
            node.height = 1 + ((BSTNodeND < K >)(node.right)).height;            
        }else if (node.right == null){
            node.height = 1 + ((BSTNodeND < K >)(node.left)).height;  
        }else{
            node.height = 1 + Math.max(((BSTNodeND < K >)(node.right)).height, ((BSTNodeND < K >)(node.left)).height);
        }
    }
      
   } // end class BinarySearchTreeND

   class BSTNodeND 
         < L extends Comparable< ? super L > > 
   {
      L data;
      protected int height = 0;
      BSTNodeND < L > left = null, right = null, parent = null;
      
      BSTNodeND (L d) {
         data = d;}
         
      BSTNodeND (L d, BSTNodeND p) {
         data = d;
         parent = p;
      } // end data + parent
      
   } // end class BSTNodeND
   
// A class that can be used with the BinarySearchTreeND data structure
// Notice the use of the generic parameter Example
   class Example implements Comparable < Example > {
      String data;      
      public Example (String d) {
         data = d;}
    
   // you, of course, will want a more interesting compareTo method
      public int compareTo (Example e) {
         return data.compareTo (e.data);
      } // end compareTo method
      
      public String toString () {
         return data;
      } // end toString
   
   } // end class Example