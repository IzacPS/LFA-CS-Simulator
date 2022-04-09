var viz = new Viz();
const mapNode = new Map(); 

function mapNodes(graph_children){
    if(mapNode.size > 0) mapNode.clear();
    for(let item of graph_children){
        let key = item.getElementsByTagName('text')[0];
        let value = item.getElementsByTagName('ellipse')[0];
    
        if(!key || !value)
            continue;
        mapNode.set(key.innerHTML, value);
    }
}

function setVisualGraph(str){
    viz.renderSVGElement(str)
    .then(function(element){
        const root = document.getElementById('graphVisualization');
        root.appendChild(element);
        const graph = root.getElementsByClassName('graph')[0];
        mapNodes(graph.children);
    }).catch(error => {
        // Create a new Viz instance (@see Caveats page for more info)
        viz = new Viz();
    
        // Possibly display the error
        console.error(error);
      });
}

function clearVisualization(){
    const el = document.getElementById('graphVisualization');
    if(el.lastElementChild){
        el.removeChild(el.lastElementChild);
    }
}

function colorStartState(state){
        let value = mapNode.get(state);
        if(value) value.setAttribute('fill','rgb(180,180,180)');
}

function colorVisualizationNextStep(currentState, nextState){
    let cur = currentState.split(',');
    let next = nextState.split(',');
    for(let i = 0; i < cur.length; i++){
        let value = mapNode.get(cur[i]);
        if(value) value.setAttribute('fill','none');
    }
    for(let i = 0; i < next.length; i++){
        let value = mapNode.get(next[i]);
        if(value) value.setAttribute('fill', 'rgb(180,180,180)');
    }
}