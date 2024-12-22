package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Node;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.pair.ReadablePair;

public @Service class SelectionSimplifier {
    private static volatile @Service SelectionSimplifier instance;

    public static @Service SelectionSimplifier getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new SelectionSimplifier();
                }
            }
        }
        return instance;
    }

    private SelectionSimplifier() {
    }

    /**
     * Exclude child nodes where their parent is selected.
     * Exclude duplicate nodes.
     * Nodes should not have empty paths.
     * @param nodes list of selected nodes
     * @param progress progress for tracking asynchronous jobs
     */
    public @Mandatory List<Node> simplify(
        @Mandatory List<Node> nodes,
        @Mandatory Progress progress
    ) {
        List<Pair<Node, Boolean>> markedNodes = markTrue(nodes, progress);
        markRedundantFalse(markedNodes, 0, progress);
        return getMarkedTrue(markedNodes, progress);
    }

    private @Mandatory List<Pair<Node, Boolean>> markTrue(
        @Mandatory List<Node> nodes,
        @Mandatory Progress progress
    ) {
        List<Pair<Node, Boolean>> markedNodes = new List<>();
        for (Node node : nodes) {
            markedNodes.addLast(new Pair<>(node, true));
            progress.step();
        }
        return markedNodes;
    }

    private @Mandatory List<Node> getMarkedTrue(
        @Mandatory List<Pair<Node, Boolean>> markedNodes,
        @Mandatory Progress progress
    ) {
        List<Node> nodes = new List<>();
        for (Pair<Node, Boolean> markedNode : markedNodes) {
            if (markedNode.getValue()) {
                nodes.addLast(markedNode.getKey());
            }
            progress.step();
        }
        return nodes;
    }

    private void markRedundantFalse(
        @Mandatory List<Pair<Node, Boolean>> markedNodes,
        int i,
        @Mandatory Progress progress
    ) {
        Map<String, List<Pair<Node, Boolean>>> map = new Map<>();
        for (Pair<Node, Boolean> currentMarkedNode : markedNodes) {
            if (isParentNode(currentMarkedNode, i)) {
                markFalse(markedNodes);
                currentMarkedNode.setValue(true);
                progress.step();
                return;
            } else {
                String name = currentMarkedNode.getKey().getPath().getName(i).toString();
                List<Pair<Node, Boolean>> group = map.getOrCreate(name, () -> new List<>());
                group.addLast(currentMarkedNode);
                progress.step();
            }
        }

        for (ReadablePair<String, List<Pair<Node, Boolean>>> entry : map) {
            if (entry.getValue().count() > 1) {
                markRedundantFalse(entry.getValue(), i + 1, progress);
            }
            progress.step();
        }
    }

    private boolean isParentNode(@Mandatory Pair<Node, Boolean> markedNode, int i) {
        return i >= markedNode.getKey().getPath().getNameCount();
    }

    private void markFalse(@Mandatory List<Pair<Node, Boolean>> markedNodes) {
        for (Pair<Node, Boolean> markedNode : markedNodes) {
            markedNode.setValue(false);
        }
    }
}
